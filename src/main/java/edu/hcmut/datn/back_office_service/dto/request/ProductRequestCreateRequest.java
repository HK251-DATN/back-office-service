package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.common.enums.Unit;
import edu.hcmut.datn.back_office_service.dao.ProductRequest;

public class ProductRequestCreateRequest {

    private Unit unit;
    private Long quantity;
    private Long requiredAfterDays;
    private Long prodGenId;
    private Long eventId;

    public ProductRequest toEntity() {
        return new ProductRequest(unit, quantity, requiredAfterDays, prodGenId, eventId);
    }
}
