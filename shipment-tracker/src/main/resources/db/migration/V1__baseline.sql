CREATE TABLE shipments (
                           id              UUID PRIMARY KEY,
                           status          VARCHAR(30) NOT NULL,
                           origin_lat      DOUBLE PRECISION NOT NULL,
                           origin_lng      DOUBLE PRECISION NOT NULL,
                           destination_lat DOUBLE PRECISION NOT NULL,
                           destination_lng DOUBLE PRECISION NOT NULL,
                           customer_id     UUID NOT NULL,
                           created_at      TIMESTAMPTZ NOT NULL
);

CREATE TABLE idempotency_keys (
                                  key         VARCHAR(255) PRIMARY KEY,
                                  shipment_id UUID NOT NULL,
                                  created_at  TIMESTAMPTZ NOT NULL
);