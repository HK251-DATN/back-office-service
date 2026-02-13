package edu.hcmut.datn.back_office_service.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProductGeneral {
    
    @Column(name = "prod_gen_id")
    @Id
    private Long prodGenId;
    
    @Column(name = "prod_name")
    private String prodName;
    
    @Column(name = "updated_at")
    private Long updatedAt;
    
    @Column(name = "created_at")
    private Long createdAt;
    
    @Column(name = "preorder_policy_id")
    private Long preorderPolicyId;
    
    @Column(name = "enterprise_store_id")
    private Long enterpriseStoreId;
}
