package com.livetrack.shipment_tracker.application.usecase;

import com.livetrack.shipment_tracker.domain.Shipment;
import com.livetrack.shipment_tracker.domain.exception.ShipmentNotFoundException;
import com.livetrack.shipment_tracker.domain.port.ShipmentRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetShipmentUseCase {

    private final ShipmentRepository repository;

    public GetShipmentUseCase(ShipmentRepository repository) {
        this.repository = repository;
    }

    public Shipment execute(UUID shipmentId) {
        return repository.findById(shipmentId)
                .orElseThrow(() -> new ShipmentNotFoundException(shipmentId));
    }
}