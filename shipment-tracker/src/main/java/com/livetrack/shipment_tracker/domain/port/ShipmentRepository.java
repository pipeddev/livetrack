package com.livetrack.shipment_tracker.domain.port;

import com.livetrack.shipment_tracker.domain.Shipment;

import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository {
    Shipment save(Shipment shipment);
    Optional<Shipment> findById(UUID id);
}


