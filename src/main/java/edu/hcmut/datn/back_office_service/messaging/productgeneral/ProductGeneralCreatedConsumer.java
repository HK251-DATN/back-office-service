package edu.hcmut.datn.back_office_service.messaging.productgeneral;

import edu.hcmut.datn.back_office_service.service.ProductGeneralService;
import edu.hcmut.datn.back_office_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ProductGeneralCreatedConsumer {

    private final ProductGeneralService productGeneralService;

    @KafkaListener(topics = "product-general-events")
    public void consume(ProductGeneralCreatedEvent event) {
        log.info("Received event: {}", event);

        try {
            productGeneralService.create(event.toProductGeneralEntity());

            log.info("Create user {} success", event.getProdGenId());
        } catch (Exception e) {
            log.error("Create user {} fail due to: {}", event.getProdGenId(), e.getMessage());
        }
    }
}
