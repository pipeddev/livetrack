package com.livetrack.shipment_tracker.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
@Getter
public class IdempotencyKeyEntity {

    @Id
    private String key;

    private UUID shipmentId;
    private Instant createdAt;

    protected IdempotencyKeyEntity() {}

    public IdempotencyKeyEntity(String key, UUID shipmentId, Instant createdAt) {
        this.key = key;
        this.shipmentId = shipmentId;
        this.createdAt = createdAt;
    }

    // getters
}
