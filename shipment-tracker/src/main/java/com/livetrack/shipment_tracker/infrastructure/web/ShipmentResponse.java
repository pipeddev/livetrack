package com.livetrack.shipment_tracker.infrastructure.web;

import com.livetrack.shipment_tracker.domain.Shipment;
import java.time.Instant;
import java.util.UUID;

public record ShipmentResponse(UUID id, String status, Instant createdAt,
                               LocationResponse lastLocation, Instant lastLocationAt) {

    public record LocationResponse(double lat, double lng) {}

    public static ShipmentResponse from(Shipment s) {
        LocationResponse loc = s.getLastLocation() == null ? null
                : new LocationResponse(s.getLastLocation().lat(), s.getLastLocation().lng());
        return new ShipmentResponse(s.getId(), s.getStatus().name(), s.getCreatedAt(),
                loc, s.getLastLocationAt());
    }
}
