package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Order {

    @Column(name = "order_id")
    @Id
    @Getter
    private Long orderId;

    @Column(name = "status")
    @Getter
    @Setter
    private OrderStatus status;

    @Column(name = "owned_by")
    @Getter
    private Long ownedBy;

    @Column(name = "confirmed_by")
    @Getter
    @Setter
    private Long confirmedBy;

    @Column(name = "packaged_by")
    @Getter
    @Setter
    private Long packagedBy;

    @Column(name = "shipped_by")
    @Getter
    @Setter
    private Long shippedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Order() {}

    // Contructor for creating purpose
    public Order(Long orderId, OrderStatus status, Long ownedBy, Long confirmedBy, Long packagedBy, Long shippedBy) {
        this.orderId = orderId;
        this.status = status;
        this.ownedBy = ownedBy;
        this.confirmedBy = confirmedBy;
        this.packagedBy = packagedBy;
        this.shippedBy = shippedBy;
    }

    // Contructor for updating purpose
    public Order(OrderStatus status, Long confirmedBy, Long packagedBy, Long shippedBy) {
        this.status = status;
        this.confirmedBy = confirmedBy;
        this.packagedBy = packagedBy;
        this.shippedBy = shippedBy;
    }
}
