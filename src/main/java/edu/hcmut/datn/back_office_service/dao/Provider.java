package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.Bank;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Provider {
    
    @Column(name="provider_id")
    @Id
    private Long providerId;
    
    @Column(name="reputation_point")
    private Long reputationPoint;
    
    @Column(name="verification_status")
    private VerificationStatus verificationStatus;
    
    @Column(name="bank_id")
    private Bank bankId;
    
    @Column(name="bank_num")
    private String bankNum;
    
    @Column(name="user_id")
    private Long userId;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
