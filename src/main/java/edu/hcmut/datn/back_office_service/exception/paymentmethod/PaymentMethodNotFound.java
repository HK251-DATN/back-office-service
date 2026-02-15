package edu.hcmut.datn.back_office_service.exception.paymentmethod;

public class PaymentMethodNotFound extends RuntimeException {
    public PaymentMethodNotFound(String message) {
        super(message);
    }
}
