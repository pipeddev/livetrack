import { Module } from '@nestjs/common';
import { ShipmentTrackerClient } from './shipment-tracker.client.js';
import { ShipmentsController } from './shipments.controller.js';

@Module({
  controllers: [ShipmentsController],
  providers: [ShipmentTrackerClient],
})
export class ShipmentsModule {}
