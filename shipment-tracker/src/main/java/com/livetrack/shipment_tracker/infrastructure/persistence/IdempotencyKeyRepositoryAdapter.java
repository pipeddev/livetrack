package com.livetrack.shipment_tracker.infrastructure.persistence;

import com.livetrack.shipment_tracker.domain.port.IdempotencyKeyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class IdempotencyKeyRepositoryAdapter implements IdempotencyKeyRepository {

    private final IdempotencyKeyJpaRepository jpaRepository;

    public IdempotencyKeyRepositoryAdapter(IdempotencyKeyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<UUID> findShipmentIdByKey(String key) {
        return jpaRepository.findById(key).map(IdempotencyKeyEntity::getShipmentId);
    }

    @Override
    public void save(String key, UUID shipmentId) {
        try {
            jpaRepository.save(new IdempotencyKeyEntity(key, shipmentId, Instant.now()));
        } catch (DataIntegrityViolationException e) {
            // race condition entre dos requests concurrentes con la misma key — ver nota de la respuesta anterior
        }
    }
}
