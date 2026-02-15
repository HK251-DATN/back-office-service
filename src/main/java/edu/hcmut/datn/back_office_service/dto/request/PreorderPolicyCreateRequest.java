package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.dao.PreorderPolicy;

public class PreorderPolicyCreateRequest {
    private Boolean isActive;
    private Boolean requirePayment;
    private Long depositPercentage;
    private Long minPreorderDay;
    private Boolean allowCancel;
    private String notes;
    private Long cancelDeadline;
    private Long createdBy;

    public PreorderPolicy toEntity() {
        return new PreorderPolicy(isActive, requirePayment, depositPercentage, minPreorderDay, allowCancel, notes, cancelDeadline, createdBy);
    }
}
