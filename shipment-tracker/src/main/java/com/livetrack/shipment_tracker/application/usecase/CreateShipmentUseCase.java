package com.livetrack.shipment_tracker.application.usecase;

import com.livetrack.shipment_tracker.application.command.CreateShipmentCommand;
import com.livetrack.shipment_tracker.domain.*;
import com.livetrack.shipment_tracker.domain.event.ShipmentEvent;
import com.livetrack.shipment_tracker.domain.port.EventPublisher;
import com.livetrack.shipment_tracker.domain.port.IdempotencyKeyRepository;
import com.livetrack.shipment_tracker.domain.port.ShipmentRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class CreateShipmentUseCase {
    private final ShipmentRepository shipmentRepository;
    private final EventPublisher publisher;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public CreateShipmentUseCase(ShipmentRepository shipmentRepository,
                                 EventPublisher publisher,
                                 IdempotencyKeyRepository idempotencyKeyRepository) {
        this.shipmentRepository = shipmentRepository;
        this.publisher = publisher;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    public Shipment execute(CreateShipmentCommand command, String idempotencyKey) {
        var existingShipmentId = idempotencyKeyRepository.findShipmentIdByKey(idempotencyKey);
        if (existingShipmentId.isPresent()) {
            return shipmentRepository.findById(existingShipmentId.get())
                    .orElseThrow(() -> new IllegalStateException("Idempotency key apunta a un shipment inexistente"));
        }

        Shipment shipment = new Shipment(
                UUID.randomUUID(), command.origin(), command.destination(), command.customerId()
        );
        Shipment saved = shipmentRepository.save(shipment);
        publisher.publish(ShipmentEvent.created(saved));
        idempotencyKeyRepository.save(idempotencyKey, saved.getId());

        return saved;
    }
}
