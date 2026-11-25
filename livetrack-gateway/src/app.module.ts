import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { ShipmentsModule } from './shipments/shipments.module.js';
import { TrackingModule } from './tracking/tracking.module.js';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    ShipmentsModule,
    TrackingModule,
  ],
})
export class AppModule {}
