package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.PaymentProvider;
import edu.hcmut.datn.back_office_service.common.enums.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PaymentMethod {
    
    @Column(name="payment_method_id")
    @Id
    private Long paymentMethodId;
    
    @Column(name="payment_type")
    private PaymentType paymentType;
    
    @Column(name="payment_provider")
    private PaymentProvider paymentProvider;
    
    @Column(name="account_num")
    private String accountNum;
    
    @Column(name="is_active")
    private Boolean isActive;
    
    @Column(name="is_default")
    private Boolean isDefault;
    
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @Column(name="buyer_id")
    private Long buyerId;
}
