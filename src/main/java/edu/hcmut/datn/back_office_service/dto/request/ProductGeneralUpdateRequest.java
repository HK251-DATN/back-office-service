package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.dao.ProductGeneral;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductGeneralUpdateRequest {
    private String prodName;
    private Long preorderPolicyId;
    private Long enterpriseStoreId;

    public ProductGeneral toEntity() {
        return new ProductGeneral(prodName, preorderPolicyId, enterpriseStoreId);
    }
}
