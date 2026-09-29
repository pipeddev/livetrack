package com.livetrack.shipment_tracker.application.usecase;

import com.livetrack.shipment_tracker.domain.*;
import com.livetrack.shipment_tracker.domain.event.ShipmentEvent;
import com.livetrack.shipment_tracker.domain.port.EventPublisher;
import com.livetrack.shipment_tracker.domain.port.ShipmentRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CancelShipmentUseCase {

    private final ShipmentRepository repository;
    private final EventPublisher publisher;

    public CancelShipmentUseCase(ShipmentRepository repository, EventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void execute(UUID shipmentId, String reason) {
        Shipment shipment = repository.findById(shipmentId)
                .orElseThrow(() -> new IllegalStateException("Shipment no encontrado: " + shipmentId));

        shipment.cancel();
        repository.save(shipment);
        publisher.publish(ShipmentEvent.cancelled(shipment, reason));
    }
}