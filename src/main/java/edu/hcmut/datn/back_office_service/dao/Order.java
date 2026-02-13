package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Order {
    
    @Column(name = "order_id")
    @Id
    private Long orderId;
    
    @Column(name = "status")
    private OrderStatus status;
    
    @Column(name = "owned_by")
    private Long ownedBy;
    
    @Column(name = "confirmed_by")
    private Long confirmedBy;
    
    @Column(name = "packaged_by")
    private Long packagedBy;
    
    @Column(name = "shipped_by")
    private Long shippedBy;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
