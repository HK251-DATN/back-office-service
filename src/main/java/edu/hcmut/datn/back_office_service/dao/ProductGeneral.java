package edu.hcmut.datn.back_office_service.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class ProductGeneral {

    @Column(name = "prod_gen_id")
    @Id
    @Getter
    private Long prodGenId;

    @Column(name = "prod_name")
    @Getter
    @Setter
    private String prodName;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "preorder_policy_id")
    @Getter
    @Setter
    private Long preorderPolicyId;

    @Column(name = "enterprise_store_id")
    @Getter
    @Setter
    private Long enterpriseStoreId;

    protected ProductGeneral() {}

    public ProductGeneral(Long prodGenId, String prodName, Long preorderPolicyId, Long enterpriseStoreId) {
        this.prodGenId = prodGenId;
        this.prodName = prodName;
        this.preorderPolicyId = preorderPolicyId;
        this.enterpriseStoreId = enterpriseStoreId;
    }

    public ProductGeneral(String prodName, Long preorderPolicyId, Long enterpriseStoreId) {
        this.prodName = prodName;
        this.preorderPolicyId = preorderPolicyId;
        this.enterpriseStoreId = enterpriseStoreId;
    }
}
