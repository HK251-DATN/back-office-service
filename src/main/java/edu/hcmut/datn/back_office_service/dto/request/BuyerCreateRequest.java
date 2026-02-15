package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.dao.Buyer;

public class BuyerCreateRequest {
    private Long userId;

    public Buyer toEntity() {
        return new Buyer(userId);
    }
}
