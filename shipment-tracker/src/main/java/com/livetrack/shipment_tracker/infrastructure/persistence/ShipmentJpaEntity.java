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

    private Double lastLat;          // wrapper: null mientras no haya ubicaciones
    private Double lastLng;
    private long lastSequence;
    private Instant lastLocationAt;


    @Version
    private Long version;

    protected ShipmentJpaEntity() {
        // constructor vacío requerido por JPA
    }

    public ShipmentJpaEntity(UUID id, ShipmentStatus status, double originLat, double originLng,
                             double destinationLat, double destinationLng,
                             UUID customerId, Instant createdAt,
                             Double lastLat, Double lastLng, long lastSequence,
                             Instant lastLocationAt, Long version) {
        this.id = id;
        this.status = status;
        this.originLat = originLat;
        this.originLng = originLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.lastLat = lastLat;
        this.lastLng = lastLng;
        this.lastSequence = lastSequence;
        this.lastLocationAt = lastLocationAt;
        this.version = version;
    }

    public UUID getId() { return id; }
    public ShipmentStatus getStatus() { return status; }
    public double getOriginLat() { return originLat; }
    public double getOriginLng() { return originLng; }
    public double getDestinationLat() { return destinationLat; }
    public double getDestinationLng() { return destinationLng; }
    public UUID getCustomerId() { return customerId; }
    public Instant getCreatedAt() { return createdAt; }
    public Double getLastLat() { return lastLat; }
    public Double getLastLng() { return lastLng; }
    public long getLastSequence() { return lastSequence; }
    public Instant getLastLocationAt() { return lastLocationAt; }
    public Long getVersion() { return version; }
}
