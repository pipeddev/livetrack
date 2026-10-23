package com.livetrack.shipment_tracker.infrastructure.messaging;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.livetrack.shipment_tracker.application.usecase.UpdateShipmentLocationUseCase;
import com.livetrack.shipment_tracker.domain.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;

@Component
public class LocationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(LocationEventConsumer.class);
    private final ObjectMapper objectMapper;
    private final UpdateShipmentLocationUseCase useCase;

    public LocationEventConsumer(ObjectMapper objectMapper, UpdateShipmentLocationUseCase useCase) {
        this.objectMapper = objectMapper;
        this.useCase = useCase;
    }

    @KafkaListener(topics = "location-events", groupId = "shipment-tracker-location")
    public void listen(String rawMessage) {
        JsonNode event;
        try {
            event = objectMapper.readTree(rawMessage);
        } catch (Exception e) {
            log.error("Mensaje inválido en location-events, se descarta: {}", rawMessage);
            return; // reintentar un JSON roto no lo arregla
        }
        if (!"driver.location_updated".equals(event.path("eventType").asText())) {
            return;
        }
        JsonNode p = event.get("payload");
        useCase.execute(
                UUID.fromString(event.get("shipmentId").asText()),
                new Coordinates(p.get("lat").asDouble(), p.get("lng").asDouble()),
                p.get("sequence").asLong(),
                Instant.parse(p.get("recordedAt").asText()));
        // los conflictos de @Version suben a Spring Kafka, que reintenta
    }
}
