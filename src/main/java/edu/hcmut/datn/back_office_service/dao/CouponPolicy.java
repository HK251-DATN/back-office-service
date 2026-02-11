package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;
import java.util.List;

import edu.hcmut.datn.back_office_service.common.enums.DiscountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CouponPolicy {

    @Column(name = "coupon_policy_id")
    @Id
    private Long couponPolicyId;

    @Column(name = "applicable_cate_ids")
    private List<Long> applicableCateIds;

    @Column(name = "discount_type")
    private DiscountType discountType;

    @Column(name = "discount_val")
    private Long discountVal;

    @Column(name = "max_discount_amount")
    private Long maxDiscountAmount;

    @Column(name = "min_order_value")
    private Long minOrderValue;

    @Column(name = "max_uses_per_acc")
    private Long maxUsesPerAcc;

    @Column(name = "cur_total_uses")
    private Long curTotalUses;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

}
