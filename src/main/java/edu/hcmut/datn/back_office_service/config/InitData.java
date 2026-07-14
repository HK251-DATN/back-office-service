package edu.hcmut.datn.back_office_service.config;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.hcmut.datn.back_office_service.common.enums.PaymentProvider;
import edu.hcmut.datn.back_office_service.common.enums.PaymentType;
import edu.hcmut.datn.back_office_service.common.enums.Unit;
import lombok.Getter;
import lombok.Setter;

/**
 * Binds {@code src/main/resources/init_data.json} for {@link DataSeeder}.
 *
 * Categories, SubSubcategories and ProductGenerals reference each other via
 * per-list JSON ids (id/subcategoryId/subSubcategoryId/belongToCategoryId) rather
 * than real DB ids, since the real ids are only assigned once each row is saved.
 */
@Getter
@Setter
public class InitData {

    @JsonProperty("Categories")
    private List<CategorySeed> categories;

    @JsonProperty("SubSubcategories")
    private List<SubSubcategorySeed> subSubcategories;

    @JsonProperty("PaymentMethods")
    private List<PaymentMethodSeed> paymentMethods;

    @JsonProperty("ProductGenerals")
    private List<ProductGeneralSeed> productGenerals;

    @Getter
    @Setter
    public static class CategorySeed {
        private int id;
        private String name;
        private String description;
        private Integer displayOrder;
        private String iconUrl;
        private String isSubCategory;
        private Integer belongToCategoryId;
    }

    @Getter
    @Setter
    public static class SubSubcategorySeed {
        private int id;
        private String name;
        private String description;
        private String iconUrl;
        private int subcategoryId;
        private Integer avgShelfDays;
    }

    @Getter
    @Setter
    public static class PaymentMethodSeed {
        private PaymentType paymentType;
        private PaymentProvider paymentProvider;
        private String accountNum;
        @JsonProperty("isActive")
        private boolean active;
    }

    @Getter
    @Setter
    public static class ProductGeneralSeed {
        private String name;
        private String description;
        private String[] tags;
        private Unit unit;
        private Long unitQuantity;
        private int subSubcategoryId;
    }
}
