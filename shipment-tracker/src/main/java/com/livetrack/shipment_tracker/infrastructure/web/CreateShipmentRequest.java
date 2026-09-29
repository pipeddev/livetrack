package com.livetrack.shipment_tracker.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateShipmentRequest(
        @NotNull CoordinatesDto origin,
        @NotNull CoordinatesDto destination,
        @NotNull UUID customerId
) {
    public record CoordinatesDto(double lat, double lng) {}
}
