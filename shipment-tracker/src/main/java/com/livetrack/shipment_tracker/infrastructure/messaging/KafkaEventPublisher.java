package com.livetrack.shipment_tracker.infrastructure.messaging;

import com.livetrack.shipment_tracker.domain.port.EventPublisher;
import com.livetrack.shipment_tracker.domain.event.ShipmentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private static final String TOPIC = "shipment-events";

    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, ShipmentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(ShipmentEvent event) {
        // usamos shipmentId como key para que todos los eventos de un mismo envío
        // caigan en la misma partición y se procesen en orden
        kafkaTemplate.send(TOPIC, event.shipmentId().toString(), event);
    }
}
