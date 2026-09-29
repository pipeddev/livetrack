package com.livetrack.shipment_tracker.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Shipment {
    private final UUID id;
    @Setter
    private ShipmentStatus status;
    private final Coordinates origin;
    private final Coordinates destination;
    private final UUID customerId;
    private final Instant createdAt;


    public Shipment(UUID id, Coordinates origin, Coordinates destination, UUID customerId) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.customerId = customerId;
        this.status = ShipmentStatus.CREATED;
        this.createdAt = Instant.now();
    }

    private Shipment(UUID id, ShipmentStatus status, Coordinates origin, Coordinates destination,
                     UUID customerId, Instant createdAt) {
        this.id = id;
        this.status = status;
        this.origin = origin;
        this.destination = destination;
        this.customerId = customerId;
        this.createdAt = createdAt;
    }

    public static Shipment reconstruct(UUID id, ShipmentStatus status, Coordinates origin,
                                       Coordinates destination, UUID customerId, Instant createdAt) {
        return new Shipment(id, status, origin, destination, customerId, createdAt);
    }

    public void cancel() {
        this.status = ShipmentStatus.CANCELLED;
    }

    public void assignDriver() {
        this.status = ShipmentStatus.DRIVER_ASSIGNED;
    }
}
