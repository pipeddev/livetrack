import { Logger } from '@nestjs/common';
import {
  ConnectedSocket,
  MessageBody,
  OnGatewayConnection,
  OnGatewayDisconnect,
  SubscribeMessage,
  WebSocketGateway,
  WebSocketServer,
} from '@nestjs/websockets';
import { Namespace, Socket } from 'socket.io';

const UUID_RE =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const room = (shipmentId: string) => `shipment:${shipmentId}`;

@WebSocketGateway({ namespace: '/tracking', cors: { origin: '*' } })
export class TrackingGateway
  implements OnGatewayConnection, OnGatewayDisconnect
{
  private readonly logger = new Logger(TrackingGateway.name);

  @WebSocketServer()
  private server!: Namespace;

  handleConnection(client: Socket) {
    this.logger.log(`conectado ${client.id}`);
  }

  handleDisconnect(client: Socket) {
    this.logger.log(`desconectado ${client.id}`);
  }

  @SubscribeMessage('subscribe')
  subscribe(
    @ConnectedSocket() client: Socket,
    @MessageBody() body: { shipmentId: string },
  ) {
    this.logger.debug(`subscribe ${client.id} → ${body?.shipmentId}`);
    if (!UUID_RE.test(body?.shipmentId ?? '')) {
      return { event: 'error', data: 'shipmentId inválido' };
    }
    client.join(room(body.shipmentId));
    return { event: 'subscribed', data: body.shipmentId };
  }

  @SubscribeMessage('unsubscribe')
  unsubscribe(
    @ConnectedSocket() client: Socket,
    @MessageBody() body: { shipmentId: string },
  ) {
    client.leave(room(body.shipmentId));
    return { event: 'unsubscribed', data: body.shipmentId };
  }

  emitLocation(shipmentId: string, location: unknown) {
    const members = this.server.adapter.rooms.get(room(shipmentId))?.size ?? 0;
    this.logger.debug(`emit location ${shipmentId} → ${members} cliente(s)`);
    this.server.to(room(shipmentId)).emit('location', location);
  }

  emitStatus(shipmentId: string, status: string) {
    this.server.to(room(shipmentId)).emit('status', { shipmentId, status });
  }
}
