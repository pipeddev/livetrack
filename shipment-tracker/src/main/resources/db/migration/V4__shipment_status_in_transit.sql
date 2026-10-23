ALTER TABLE shipments DROP CONSTRAINT shipments_status_check;

ALTER TABLE shipments ADD CONSTRAINT shipments_status_check
    CHECK (status IN ('CREATED', 'DRIVER_ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED'));