package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.common.enums.MembershipLevel;
import edu.hcmut.datn.back_office_service.dao.Buyer;

public class BuyerUpdateRequest {
    private Long loyaltyPoint;
    private Long totalOrders;
    private Long totalSpentAmount;
    private MembershipLevel membershipLevel;

    public Buyer toEntity() {
        return new Buyer(loyaltyPoint, totalOrders, totalSpentAmount, membershipLevel);
    }
}

