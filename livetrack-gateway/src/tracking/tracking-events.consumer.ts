import {
  Injectable,
  Logger,
  OnModuleDestroy,
  OnModuleInit,
} from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { hostname } from 'node:os';
import { TrackingGateway } from './tracking.gateway.js';
import kafkajs, { type Consumer } from 'kafkajs';
import SnappyCodec from 'kafkajs-snappy';

const { Kafka, CompressionCodecs, CompressionTypes } = kafkajs;

CompressionCodecs[CompressionTypes.Snappy] = SnappyCodec;
interface EventEnvelope {
  eventType: string;
  shipmentId: string;
  payload: Record<string, unknown>;
}

@Injectable()
export class TrackingEventsConsumer implements OnModuleInit, OnModuleDestroy {
  private readonly logger = new Logger(TrackingEventsConsumer.name);
  private consumer?: Consumer;

  constructor(
    private readonly config: ConfigService,
    private readonly gateway: TrackingGateway,
  ) {}

  async onModuleInit() {
    const kafka = new Kafka({
      clientId: 'livetrack-gateway',
      brokers: this.config.get('KAFKA_BROKERS', 'localhost:19092').split(','),
    });

    // un group por réplica → cada instancia recibe TODOS los eventos
    this.consumer = kafka.consumer({
      groupId: `livetrack-gateway-${hostname()}`,
    });
    this.consumer.on(this.consumer.events.CRASH, ({ payload }) => {
      this.logger.error(
        `consumer caído (restart=${payload.restart}): ${payload.error.message}`,
      );
      if (!payload.restart) process.exit(1); // que Docker/K8s lo reinicie
    });
    await this.consumer.connect();
    await this.consumer.subscribe({
      topics: ['location-events', 'driver-events', 'shipment-events'],
      fromBeginning: false,
    });

    await this.consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        const event = this.parse(message.value);
        this.logger.debug(
          `${topic}[${partition}] ${event?.eventType} ${event?.shipmentId}`,
        );
        if (event) this.route(event);
      },
    });
    this.logger.log(
      'escuchando location-events, driver-events, shipment-events',
    );
  }

  async onModuleDestroy() {
    await this.consumer?.disconnect();
  }

  private parse(value: Buffer | null): EventEnvelope | null {
    try {
      return value ? (JSON.parse(value.toString()) as EventEnvelope) : null;
    } catch {
      this.logger.warn('mensaje inválido, se descarta');
      return null;
    }
  }

  private route({ eventType, shipmentId, payload }: EventEnvelope) {
    switch (eventType) {
      case 'driver.location_updated':
        this.gateway.emitLocation(shipmentId, payload);
        break;
      case 'driver.reserved':
        this.gateway.emitStatus(shipmentId, 'DRIVER_ASSIGNED');
        break;
      case 'shipment.cancelled':
        this.gateway.emitStatus(shipmentId, 'CANCELLED');
        break;
    }
  }
}
