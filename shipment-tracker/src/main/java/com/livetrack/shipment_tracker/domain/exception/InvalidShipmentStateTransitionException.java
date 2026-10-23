package com.livetrack.shipment_tracker.domain.exception;

import com.livetrack.shipment_tracker.domain.ShipmentStatus;

import java.util.UUID;

public class InvalidShipmentStateTransitionException extends RuntimeException {
    public InvalidShipmentStateTransitionException(UUID id, ShipmentStatus from, ShipmentStatus to) {
        super("Shipment %s no puede pasar de %s a %s".formatted(id, from, to));
    }
}