import { Module } from '@nestjs/common';
import { TrackingEventsConsumer } from './tracking-events.consumer.js';
import { TrackingGateway } from './tracking.gateway.js';

@Module({
  providers: [TrackingGateway, TrackingEventsConsumer],
})
export class TrackingModule {}
