package com.livetrack.shipment_tracker.domain.exception;

import java.util.UUID;

public class ShipmentNotFoundException extends RuntimeException {
    public ShipmentNotFoundException(UUID shipmentId) {
        super("Shipment no encontrado: " + shipmentId);
    }
}
