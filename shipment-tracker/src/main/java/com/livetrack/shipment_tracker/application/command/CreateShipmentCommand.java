package com.livetrack.shipment_tracker.application.command;

import com.livetrack.shipment_tracker.domain.Coordinates;
import java.util.UUID;

public record CreateShipmentCommand(Coordinates origin, Coordinates destination, UUID customerId) {
}
