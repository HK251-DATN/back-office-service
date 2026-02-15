package edu.hcmut.datn.back_office_service.dto.request;

import java.util.List;

import edu.hcmut.datn.back_office_service.common.enums.DiscountType;
import edu.hcmut.datn.back_office_service.dao.CouponPolicy;

public class CouponPolicyUpdateRequest {
    private List<Long> applicableCateIds;
    private DiscountType discountType;
    private Long discountVal;
    private Long maxDiscountAmount;
    private Long minOrderValue;
    private Long maxUsesPerAcc;

    public CouponPolicy toEntity() {
        return new CouponPolicy(applicableCateIds, discountType, discountVal, maxDiscountAmount, minOrderValue, maxUsesPerAcc);
    }
}
