package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class PreorderPolicy {

    @Column(name="preorder_policy_id")
    @Id
    private Long preorderPolicyId;

    @Column(name="is_active")
    private Boolean isActive;

    @Column(name="require_payment")
    private Boolean requirePayment;

    @Column(name="deposit_percentage")
    private Long depositPercentage;

    @Column(name="min_preorder_day")
    private Long minPreorderDay;

    @Column(name="allow_cancel")
    private Boolean allowCancel;

    @Column(name="notes")
    private String notes;

    @Column(name="cancel_deadline")
    private Long cancelDeadline;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    @Column(name="created_by")
    private Long createdBy;

}
