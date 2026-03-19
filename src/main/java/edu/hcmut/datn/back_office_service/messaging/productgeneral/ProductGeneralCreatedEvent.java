package edu.hcmut.datn.back_office_service.messaging.productgeneral;

import edu.hcmut.datn.back_office_service.common.enums.AccountStatus;
import edu.hcmut.datn.back_office_service.common.enums.Gender;
import edu.hcmut.datn.back_office_service.dao.ProductGeneral;
import edu.hcmut.datn.back_office_service.dao.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
public class ProductGeneralCreatedEvent {

    @Getter
    private Long prodGenId;

    @Getter
    private String prodName;

    @Getter
    private String imgUrl;

    @Getter
    private String description;

    @Getter
    private Long categoryId;

    public ProductGeneral toProductGeneralEntity() {
        ProductGeneral newProduct = new ProductGeneral();

        newProduct.setProdGenId(prodGenId);
        newProduct.setProdName(prodName);
        newProduct.setImgUrl(imgUrl);
        newProduct.setDescription(description);
        newProduct.setCategoryId(categoryId);

        return newProduct;
    }
}
