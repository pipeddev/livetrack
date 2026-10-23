package com.livetrack.shipment_tracker.application.usecase;

import com.livetrack.shipment_tracker.domain.Coordinates;
import com.livetrack.shipment_tracker.domain.Shipment;
import com.livetrack.shipment_tracker.domain.ShipmentStatus;
import com.livetrack.shipment_tracker.domain.port.ShipmentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateShipmentLocationUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateShipmentLocationUseCase.class);
    private final ShipmentRepository repository;

    public UpdateShipmentLocationUseCase(ShipmentRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(UUID shipmentId, Coordinates position, long sequence, Instant recordedAt) {
        Optional<Shipment> found = repository.findById(shipmentId);
        if (found.isEmpty()) {
            log.warn("Ubicación para shipment inexistente {}", shipmentId);
            return;
        }
        Shipment shipment = found.get();
        ShipmentStatus before = shipment.getStatus();

        if (shipment.updateLocation(position, sequence, recordedAt)) {
            repository.save(shipment);
            if (before != shipment.getStatus()) {
                log.info("Shipment {} {} → {}", shipmentId, before, shipment.getStatus());
            }
        }
    }
}