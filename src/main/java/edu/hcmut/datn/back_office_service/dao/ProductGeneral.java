package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_generals")
@NoArgsConstructor
public class ProductGeneral {

    @Column(name = "prod_gen_id")
    @Id
    @Getter
    private Long prodGenId;

    @Column(name = "prod_name")
    @Getter
    @Setter
    private String prodName;

    @Column(name = "img")
    @Getter
    @Setter
    private String imgUrl;

    @Column(name = "description")
    @Getter
    @Setter
    private String description;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "preorder_policy_id")
    @Getter
    @Setter
    private Long preorderPolicyId;

    @Column(name = "enterprise_store_id")
    @Getter
    @Setter
    private Long enterpriseStoreId;

    @Column(name = "category_id")
    @Getter
    @Setter
    private Long categoryId;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now(); // Set createdAt on first save
        updatedAt = LocalDateTime.now(); // Optional: Set initial updatedAt
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now(); // Update on every save after creation
    }
}
