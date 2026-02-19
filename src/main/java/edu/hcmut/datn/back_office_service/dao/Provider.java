package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
@Table(name = "providers")
public class Provider {

    @Column(name = "provider_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long providerId;

    @Column(name = "reputation_point")
    @Setter
    @Getter
    private Long reputationPoint;

    @Column(name = "verification_status")
    @Setter
    @Getter
    private VerificationStatus verificationStatus;

    @Column(name = "bank_id")
    @Setter
    @Getter
    private Bank bankId;

    @Column(name = "bank_num")
    @Setter
    @Getter
    private String bankNum;

    @Column(name = "user_id")
    @Getter
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Provider(
            Long reputationPoint,
            VerificationStatus verificationStatus,
            Bank bankId,
            String bankNum,
            Long userId) {
        this.reputationPoint = reputationPoint;
        this.verificationStatus = verificationStatus;
        this.bankId = bankId;
        this.bankNum = bankNum;
        this.userId = userId;
    }

    public Provider(
            Long reputationPoint,
            VerificationStatus verificationStatus,
            Bank bankId,
            String bankNum) {
        this.reputationPoint = reputationPoint;
        this.verificationStatus = verificationStatus;
        this.bankId = bankId;
        this.bankNum = bankNum;
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
