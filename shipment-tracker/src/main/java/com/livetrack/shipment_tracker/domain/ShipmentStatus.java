package com.livetrack.shipment_tracker.domain;

public enum ShipmentStatus {
    CREATED,
    DRIVER_ASSIGNED,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED;

    public boolean canTransitionTo(ShipmentStatus next) {
        return switch (this) {
            case CREATED         -> next == DRIVER_ASSIGNED || next == CANCELLED;
            case DRIVER_ASSIGNED -> next == IN_TRANSIT || next == CANCELLED;
            case IN_TRANSIT      -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    public boolean isFinal() {
        return this == DELIVERED || this == CANCELLED;
    }
}
