package com.livetrack.shipment_tracker.domain;

import com.livetrack.shipment_tracker.domain.exception.InvalidShipmentStateTransitionException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class Shipment {

    private static final double ARRIVAL_THRESHOLD_KM = 0.05; // 50 m

    private final UUID id;
    private ShipmentStatus status;
    private final Coordinates origin;
    private final Coordinates destination;
    private final UUID customerId;
    private final Instant createdAt;
    private Coordinates lastLocation;
    private long lastSequence;
    private Instant lastLocationAt;
    private Long version;

    public Shipment(UUID id, Coordinates origin, Coordinates destination, UUID customerId) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.customerId = customerId;
        this.status = ShipmentStatus.CREATED;
        this.createdAt = Instant.now();
    }

    private Shipment(UUID id, ShipmentStatus status, Coordinates origin, Coordinates destination,
                     UUID customerId, Instant createdAt, Coordinates lastLocation,
                     long lastSequence, Instant lastLocationAt, Long version) {
        this.id = id;
        this.status = status;
        this.origin = origin;
        this.destination = destination;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.lastLocation = lastLocation;
        this.lastSequence = lastSequence;
        this.lastLocationAt = lastLocationAt;
        this.version = version;
    }

    public static Shipment reconstruct(UUID id, ShipmentStatus status, Coordinates origin,
                                       Coordinates destination, UUID customerId, Instant createdAt,
                                       Coordinates lastLocation, long lastSequence,
                                       Instant lastLocationAt, Long version) {
        return new Shipment(id, status, origin, destination, customerId, createdAt,
                lastLocation, lastSequence, lastLocationAt, version);
    }

    public void assignDriver() { transitionTo(ShipmentStatus.DRIVER_ASSIGNED); }
    public void startTransit() { transitionTo(ShipmentStatus.IN_TRANSIT); }
    public void deliver()      { transitionTo(ShipmentStatus.DELIVERED); }
    public void cancel()       { transitionTo(ShipmentStatus.CANCELLED); }

    private void transitionTo(ShipmentStatus next) {
        if (status == next) {
            return;
        }
        if (!status.canTransitionTo(next)) {
            throw new InvalidShipmentStateTransitionException(id, status, next);
        }
        this.status = next;
    }

    // ---- Ubicación ----

    /** @return true si la ubicación se aplicó y hay que persistir */
    public boolean updateLocation(Coordinates position, long sequence, Instant recordedAt) {
        if (status.isFinal() || sequence <= lastSequence) {
            return false; // envío cerrado, o evento duplicado/desordenado
        }
        if (status == ShipmentStatus.CREATED) {
            return false; // driver.reserved aún no se procesa (otro topic, sin orden garantizado)
        }
        if (status == ShipmentStatus.DRIVER_ASSIGNED) {
            startTransit();
        }

        this.lastLocation = position;
        this.lastSequence = sequence;
        this.lastLocationAt = recordedAt;

        if (position.distanceKmTo(destination) <= ARRIVAL_THRESHOLD_KM) {
            deliver();
        }
        return true;
    }
}
