package edu.hcmut.datn.back_office_service.messaging.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderDeliveringEvent {
    private Long orderId;
}
