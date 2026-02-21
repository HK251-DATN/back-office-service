package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.dao.ProductRequest;

public class ProductRequestUpdateRequest {
    private Long quantity;
    private Long requiredAfterDays;

    public ProductRequest toEntity() {
        return new ProductRequest(quantity, requiredAfterDays);
    }
}
