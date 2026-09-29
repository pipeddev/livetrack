package com.livetrack.shipment_tracker.infrastructure.persistence;

import com.livetrack.shipment_tracker.domain.ShipmentStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shipments")
public class ShipmentJpaEntity {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    private double originLat;
    private double originLng;
    private double destinationLat;
    private double destinationLng;

    private UUID customerId;
    private Instant createdAt;

    protected ShipmentJpaEntity() {
        // constructor vacío requerido por JPA
    }

    public ShipmentJpaEntity(UUID id, ShipmentStatus status, double originLat, double originLng,
                             double destinationLat, double destinationLng,
                             UUID customerId, Instant createdAt) {
        this.id = id;
        this.status = status;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.customerId = customerId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public ShipmentStatus getStatus() {
        return status;
    }

    public double getOriginLat() {
        return originLat;
    }

    public double getOriginLng() {
        return originLng;
    }

    public double getDestinationLat() {
        return destinationLat;
    }

    public double getDestinationLng() {
        return destinationLng;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
