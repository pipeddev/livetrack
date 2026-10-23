package com.livetrack.shipment_tracker.infrastructure.messaging;

import com.livetrack.shipment_tracker.application.usecase.CancelShipmentUseCase;
import com.livetrack.shipment_tracker.application.usecase.UpdateShipmentStatusUseCase;
import com.livetrack.shipment_tracker.domain.exception.InvalidShipmentStateTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class DriverEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DriverEventConsumer.class);

    private final CancelShipmentUseCase cancelShipmentUseCase;
    private final UpdateShipmentStatusUseCase updateShipmentStatusUseCase;
    private final ObjectMapper objectMapper;

    public DriverEventConsumer(CancelShipmentUseCase cancelShipmentUseCase,
                               UpdateShipmentStatusUseCase updateShipmentStatusUseCase,
                               ObjectMapper objectMapper) {
        this.cancelShipmentUseCase = cancelShipmentUseCase;
        this.updateShipmentStatusUseCase = updateShipmentStatusUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "driver-events", groupId = "shipment-tracker-group")
    public void listen(String rawMessage) {
        JsonNode event;
        try {
            event = objectMapper.readTree(rawMessage);
        } catch (Exception e) {
            log.error("Mensaje inválido en driver-events, se descarta: {}", rawMessage);
            return; // error permanente: reintentar no lo arregla
        }

        String eventType = event.path("eventType").asString();
        UUID shipmentId = UUID.fromString(event.get("shipmentId").asString());

        try {
            switch (eventType) {
                case "driver.reservation_failed" -> {
                    String reason = event.path("payload").path("reason").asString();
                    cancelShipmentUseCase.execute(shipmentId, reason);
                }
                case "driver.reserved" -> updateShipmentStatusUseCase.markDriverAssigned(shipmentId);
                default -> { /* evento que no nos interesa */ }
            }
        } catch (InvalidShipmentStateTransitionException e) {
            log.warn("Transición descartada para {}: {}", shipmentId, e.getMessage());
            // error permanente: el estado no va a cambiar por reintentar
        }
        // cualquier otra excepción (conflicto de @Version, DB caída) sube → Spring Kafka reintenta
    }
}