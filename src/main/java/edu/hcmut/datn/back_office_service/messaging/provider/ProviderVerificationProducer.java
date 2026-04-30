package edu.hcmut.datn.back_office_service.messaging.provider;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProviderVerificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "provider-verification-update-events";

    public void publishVerificationUpdated(ProviderVerificationUpdatedEvent event) {
        try {
            kafkaTemplate.send(TOPIC, event);
            log.info("Published ProviderVerificationUpdatedEvent for providerId={}, status={}, method={}, certificateType={}",
                    event.providerId(), event.verificationStatus(), event.verificationMethod(), event.certificateType());
        } catch (Exception e) {
            log.error("Failed to publish ProviderVerificationUpdatedEvent for providerId={}: {}",
                    event.providerId(), e.getMessage(), e);
        }
    }
}
