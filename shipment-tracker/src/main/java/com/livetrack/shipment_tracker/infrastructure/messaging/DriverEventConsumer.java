package com.livetrack.shipment_tracker.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.livetrack.shipment_tracker.application.usecase.CancelShipmentUseCase;
import com.livetrack.shipment_tracker.application.usecase.UpdateShipmentStatusUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DriverEventConsumer {

    private final CancelShipmentUseCase cancelShipmentUseCase;
    private final UpdateShipmentStatusUseCase updateShipmentStatusUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DriverEventConsumer(CancelShipmentUseCase cancelShipmentUseCase, UpdateShipmentStatusUseCase updateShipmentStatusUseCase) {
        this.cancelShipmentUseCase = cancelShipmentUseCase;
        this.updateShipmentStatusUseCase = updateShipmentStatusUseCase;
    }

    @KafkaListener(topics = "driver-events", groupId = "shipment-tracker-group")
    public void listen(String rawMessage) {
        try {
            JsonNode event = objectMapper.readTree(rawMessage);
            String eventType = event.get("eventType").asText();
            UUID shipmentId = UUID.fromString(event.get("shipmentId").asText());

            if ("driver.reservation_failed".equals(eventType)) {
                String reason = event.get("payload").get("reason").asText();
                cancelShipmentUseCase.execute(shipmentId, reason);
            } else if ("driver.reserved".equals(eventType)) {
                updateShipmentStatusUseCase.markDriverAssigned(shipmentId);
            }
        } catch (Exception e) {
            // por ahora solo logueamos; más adelante esto es candidato a dead-letter queue
            System.err.println("Error procesando evento de driver-events: " + e.getMessage());
        }
    }
}