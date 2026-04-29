package edu.hcmut.datn.back_office_service.messaging.user;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import edu.hcmut.datn.back_office_service.service.ProviderService;
import edu.hcmut.datn.back_office_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class ProviderCreatedConsumer {

    private final UserService userService;
    private final ProviderService providerService;

    @KafkaListener(topics = "provider-create-events")
    public void consume(ProviderCreatedEvent event) {
        log.info("Received provider-create-events for userId={}, isFreshAccount={}", event.getUserId(), event.isFreshAccount());

        try {
            if (event.isFreshAccount()) {
                userService.create(event.toUserEntity());
                log.info("Created user {} from provider registration", event.getUserId());
            }

            providerService.create(event.toProviderEntity());
            log.info("Created provider for userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("Failed to process provider-create-events for userId={}: {}", event.getUserId(), e.getMessage());
        }
    }
}
