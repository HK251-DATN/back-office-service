package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Provider {

    @Column(name="provider_id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Getter
    private Long providerId;

    @Column(name="reputation_point")
    @Setter
    @Getter
    private Long reputationPoint;

    @Column(name="verification_status")
    @Setter
    @Getter
    private VerificationStatus verificationStatus;

    @Column(name="bank_id")
    @Setter
    @Getter
    private Bank bankId;

    @Column(name="bank_num")
    @Setter
    @Getter
    private String bankNum;

    @Column(name="user_id")
    @Getter
    private Long userId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    protected Provider() {}

    public Provider(
            Long reputationPoint,
            VerificationStatus verificationStatus,
            Bank bankId,
            String bankNum,
            Long userId
    ) {
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
            String bankNum
    ) {
        this.reputationPoint = reputationPoint;
        this.verificationStatus = verificationStatus;
        this.bankId = bankId;
        this.bankNum = bankNum;
    }
}
