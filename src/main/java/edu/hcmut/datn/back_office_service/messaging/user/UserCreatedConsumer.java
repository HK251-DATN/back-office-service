package edu.hcmut.datn.back_office_service.messaging.user;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import edu.hcmut.datn.back_office_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class UserCreatedConsumer {

    private final UserService userService;

    @KafkaListener(topics = "user-events")
    public void consume(UserCreatedEvent event) {
        log.info("Received event: {}", event);

        try {
            userService.create(event.toUserEntity());

            log.info("Create user {} success", event.getUserId());
        } catch (Exception e) {
            log.error("Create user {} fail due to: {}", event.getUserId(), e.getMessage());
        }
    }
}
