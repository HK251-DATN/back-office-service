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
    private String imgUrl;
    private String description;
    private Long preorderPolicyId;
    private Long enterpriseStoreId;
    private Long subSubcategoryId;
    
    public ProductGeneral toEntity() {
        ProductGeneral productGeneral = new ProductGeneral();
        
        productGeneral.setProdName(prodName);
        productGeneral.setImgUrl(imgUrl);
        productGeneral.setDescription(description);
        productGeneral.setPreorderPolicyId(preorderPolicyId);
        productGeneral.setEnterpriseStoreId(enterpriseStoreId);
        productGeneral.setSubSubcategoryId(subSubcategoryId);
        
        return productGeneral;
    }
}