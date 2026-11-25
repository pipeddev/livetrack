import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';

export interface UpstreamResponse {
  status: number;
  body: unknown;
}

@Injectable()
export class ShipmentTrackerClient {
  private readonly baseUrl: string;

  constructor(config: ConfigService) {
    this.baseUrl = config.get('SHIPMENT_TRACKER_URL', 'http://localhost:8081');
  }

  async create(
    body: unknown,
    idempotencyKey?: string,
  ): Promise<UpstreamResponse> {
    const res = await fetch(`${this.baseUrl}/shipments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(idempotencyKey && { 'Idempotency-Key': idempotencyKey }),
      },
      body: JSON.stringify(body),
    });
    return { status: res.status, body: await res.json() };
  }

  async findById(id: string): Promise<UpstreamResponse> {
    const res = await fetch(
      `${this.baseUrl}/shipments/${encodeURIComponent(id)}`,
    );
    return { status: res.status, body: await res.json() };
  }
}
