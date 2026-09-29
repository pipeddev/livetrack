package com.livetrack.shipment_tracker.infrastructure.web;

import com.livetrack.shipment_tracker.domain.Shipment;
import java.time.Instant;
import java.util.UUID;

public record ShipmentResponse(UUID id, String status, Instant createdAt) {
    public static ShipmentResponse from(Shipment shipment) {
        return new ShipmentResponse(shipment.getId(), shipment.getStatus().name(), shipment.getCreatedAt());
    }
}
