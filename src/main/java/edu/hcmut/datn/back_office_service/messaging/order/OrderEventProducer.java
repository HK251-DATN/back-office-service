package edu.hcmut.datn.back_office_service.messaging.order;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderDelivering(OrderDeliveringEvent event) {
        log.info("Publishing event -> {}", event);
        kafkaTemplate.send("order-delivering-events", event.getOrderId().toString(), event);
    }

    public void publishOrderDelivered(OrderDeliveredEvent event) {
        log.info("Publishing event -> {}", event);
        kafkaTemplate.send("order-delivered-events", event.getOrderId().toString(), event);
    }
}
