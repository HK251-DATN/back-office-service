package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.VerificationMethod;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@ToString
@Table(name = "providers")
public class Provider {

    @Column(name = "provider_id")
    @Id
    @Getter
    @Setter
    private Long providerId;

    @Column(name = "reputation_point")
    @Setter
    @Getter
    private Long reputationPoint = 100L;

    @Column(name = "verification_status")
    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "verification_method")
    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private VerificationMethod verificationMethod;

    @Column(name = "bank_id")
    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Bank bankId;

    @Column(name = "bank_num")
    @Setter
    @Getter
    private String bankNum;

    @Column(name = "user_id")
    @Getter
    private Long userId;

    @Column(name = "logo_url")
    @Setter
    @Getter
    private String logoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Provider(
            Bank bankId,
            String bankNum,
            Long userId) {
        this.reputationPoint = 100L;
        this.verificationStatus = VerificationStatus.UNVERIFIED;
        this.bankId = bankId;
        this.bankNum = bankNum;
        this.userId = userId;
    }

    public Provider(
            Long reputationPoint,
            VerificationStatus verificationStatus,
            VerificationMethod verificationMethod,
            Bank bankId,
            String bankNum) {
        this.reputationPoint = reputationPoint;
        this.verificationStatus = verificationStatus;
        this.verificationMethod = verificationMethod;
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
