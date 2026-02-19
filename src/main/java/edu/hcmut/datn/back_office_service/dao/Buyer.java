package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.MembershipLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "buyers")
@NoArgsConstructor
public class Buyer {

    @Column(name = "buyer_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long buyerId;

    @Column(name = "user_id")
    @Getter
    private Long userId;

    @Column(name = "loyalty_point")
    @Getter
    @Setter
    private Long loyaltyPoint;

    @Column(name = "total_orders")
    @Getter
    @Setter
    private Long totalOrders;

    @Column(name = "total_spent_amount")
    @Getter
    @Setter
    private Long totalSpentAmount;

    @Column(name = "membership_level")
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private MembershipLevel membershipLevel;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // For creating purpose
    public Buyer(Long userId) {
        this.userId = userId;
        this.loyaltyPoint = 50L;
        this.totalOrders = 0L;
        this.totalSpentAmount = 0L;
        this.membershipLevel = MembershipLevel.NEW;
    }

    // For updating purpose
    public Buyer(Long loyaltyPoint, Long totalOrders, Long totalSpentAmount, MembershipLevel membershipLevel) {
        this.loyaltyPoint = loyaltyPoint;
        this.totalOrders = totalOrders;
        this.totalSpentAmount = totalSpentAmount;
        this.membershipLevel = membershipLevel;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Set createdAt on first save
        updatedAt = LocalDateTime.now(); // Optional: Set initial updatedAt
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // Update on every save after creation
    }
}
