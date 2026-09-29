package com.livetrack.shipment_tracker.domain.port;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyKeyRepository {
    Optional<UUID> findShipmentIdByKey(String key);
    void save(String key, UUID shipmentId);
}
