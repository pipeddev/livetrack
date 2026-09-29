package com.livetrack.shipment_tracker.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ShipmentJpaRepository extends JpaRepository<ShipmentJpaEntity, UUID> {
}