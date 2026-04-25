package edu.hcmut.datn.back_office_service.messaging.order;

import edu.hcmut.datn.back_office_service.dao.Order;
import edu.hcmut.datn.back_office_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderUpdatePackagingProgressEventConsumer {
    
    private final OrderService orderService;
    
    @KafkaListener(topics = "order-packaging-progress-update-events", groupId = "back-office-group")
    public void consume(OrderUpdatePackagingProgressEvent event) {
        log.info("Consumed message -> {}", event);
        try {
            Order order = event.toOrderEntity();
            
            orderService.update(event.getOrderId(), order);
            log.info("Order {} update packaging progress successfully, the current progress is {}%", event.getOrderId(), event.getPackagingProgress());
        } catch (Exception e) {
            log.error("Failed to confirm order {}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
