package com.livetrack.shipment_tracker.application.usecase;

import com.livetrack.shipment_tracker.domain.*;
import com.livetrack.shipment_tracker.domain.port.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateShipmentStatusUseCase {

    private final ShipmentRepository repository;

    public UpdateShipmentStatusUseCase(ShipmentRepository repository) {
        this.repository = repository;
    }

    public void markDriverAssigned(UUID shipmentId) {
        Shipment shipment = repository.findById(shipmentId)
                .orElseThrow(() -> new IllegalStateException("Shipment no encontrado: " + shipmentId));

        shipment.assignDriver();
        repository.save(shipment);
    }
}
