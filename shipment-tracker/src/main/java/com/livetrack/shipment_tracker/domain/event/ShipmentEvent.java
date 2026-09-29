package com.livetrack.shipment_tracker.domain.event;

import com.livetrack.shipment_tracker.domain.Coordinates;
import com.livetrack.shipment_tracker.domain.Shipment;

import java.time.Instant;
import java.util.UUID;

public record ShipmentEvent(UUID eventId, String eventType, Instant occurredAt, UUID shipmentId, Object payload) {

    public static ShipmentEvent created(Shipment shipment) {
        return new ShipmentEvent(
                UUID.randomUUID(),
                "shipment.created",
                Instant.now(),
                shipment.getId(),
                new CreatedPayload(shipment.getOrigin(), shipment.getDestination(), shipment.getCustomerId())
        );
    }

    public record CreatedPayload(Coordinates origin, Coordinates destination, UUID customerId) {}

    public static ShipmentEvent cancelled(Shipment shipment, String reason) {
        return new ShipmentEvent(
                UUID.randomUUID(),
                "shipment.cancelled",
                Instant.now(),
                shipment.getId(),
                new CancelledPayload(reason)
        );
    }

    public record CancelledPayload(String reason) {}
}
