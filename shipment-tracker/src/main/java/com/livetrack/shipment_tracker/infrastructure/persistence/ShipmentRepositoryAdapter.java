package com.livetrack.shipment_tracker.infrastructure.persistence;

import com.livetrack.shipment_tracker.domain.*;
import com.livetrack.shipment_tracker.domain.port.ShipmentRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class ShipmentRepositoryAdapter implements ShipmentRepository {

    private final ShipmentJpaRepository jpaRepository;

    public ShipmentRepositoryAdapter(ShipmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Shipment save(Shipment shipment) {
        ShipmentJpaEntity entity = toEntity(shipment);
        jpaRepository.save(entity);
        return shipment;
    }

    @Override
    public Optional<Shipment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private ShipmentJpaEntity toEntity(Shipment shipment) {
        Coordinates last = shipment.getLastLocation();

        return new ShipmentJpaEntity(
                shipment.getId(), shipment.getStatus(),
                shipment.getOrigin().lat(), shipment.getOrigin().lng(),
                shipment.getDestination().lat(), shipment.getDestination().lng(),
                shipment.getCustomerId(), shipment.getCreatedAt(),
                last == null ? null : last.lat(),
                last == null ? null : last.lng(),
                shipment.getLastSequence(),
                shipment.getLastLocationAt(),
                shipment.getVersion());
    }

    private Shipment toDomain(ShipmentJpaEntity entity) {
        Coordinates lastLocation = entity.getLastLat() == null ? null
                : new Coordinates(entity.getLastLat(), entity.getLastLng());

        return Shipment.reconstruct(
                entity.getId(),
                entity.getStatus(),
                new Coordinates(entity.getOriginLat(), entity.getOriginLng()),
                new Coordinates(entity.getDestinationLat(), entity.getDestinationLng()),
                entity.getCustomerId(),
                entity.getCreatedAt(),
                lastLocation,
                entity.getLastSequence(),
                entity.getLastLocationAt(),
                entity.getVersion()
        );
    }
}