import {
  Body,
  Controller,
  Get,
  Headers,
  Param,
  Post,
  Res,
} from '@nestjs/common';
import type { Response } from 'express';
import { ShipmentTrackerClient } from './shipment-tracker.client.js';

@Controller('shipments')
export class ShipmentsController {
  constructor(private readonly client: ShipmentTrackerClient) {}

  @Post()
  async create(
    @Body() body: unknown,
    @Headers('idempotency-key') idempotencyKey: string | undefined,
    @Res() res: Response,
  ) {
    const { status, body: payload } = await this.client.create(
      body,
      idempotencyKey,
    );
    res.status(status).json(payload);
  }

  @Get(':id')
  async findOne(@Param('id') id: string, @Res() res: Response) {
    const { status, body } = await this.client.findById(id);
    res.status(status).json(body);
  }
}
