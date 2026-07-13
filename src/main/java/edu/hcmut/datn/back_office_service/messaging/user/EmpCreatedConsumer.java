package edu.hcmut.datn.back_office_service.messaging.user;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import edu.hcmut.datn.back_office_service.service.EmployeeService;
import edu.hcmut.datn.back_office_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@AllArgsConstructor
public class EmpCreatedConsumer {

    private final UserService userService;
    private final EmployeeService employeeService;

    @KafkaListener(topics = "emp-create-events")
    public void consume(EmpCreatedEvent event) {
        log.info("Received event: {}", event);

        try {
            userService.create(event.toUserEntity());

            log.info("Create user {} success", event.getUserId());

            employeeService.create(event.toEmployeeEntity());

            log.info("Create employee {} success", event.getUserId());
        } catch (Exception e) {
            log.error("Create employee {} fail due to: {}", event.getUserId(), e.getMessage());
        }
    }
}
