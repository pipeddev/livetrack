package com.livetrack.shipment_tracker.domain.port;

import com.livetrack.shipment_tracker.domain.event.ShipmentEvent;

public interface EventPublisher {
    void publish(ShipmentEvent event);
}