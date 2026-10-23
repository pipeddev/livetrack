ALTER TABLE shipments
    ADD COLUMN last_lat         DOUBLE PRECISION,
    ADD COLUMN last_lng         DOUBLE PRECISION,
    ADD COLUMN last_sequence    BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN last_location_at TIMESTAMPTZ,
    ADD COLUMN version          BIGINT NOT NULL DEFAULT 0;