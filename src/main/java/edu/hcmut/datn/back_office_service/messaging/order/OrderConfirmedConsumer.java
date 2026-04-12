package edu.hcmut.datn.back_office_service.messaging.order;

import edu.hcmut.datn.back_office_service.service.OrderService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OrderConfirmedConsumer {

    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderConfirmedConsumer.class);

    @KafkaListener(topics = "order-confirmed-events", groupId = "back-office-group")
    public void consume(OrderConfirmedEvent event) {
        log.info("Consumed message -> {}", event);
        try {
            orderService.update(event.getOrderId(), event.toOrderEntity());
            log.info("Order {} confirmed successfully", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to confirm order {}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
