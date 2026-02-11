package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EnterpriseStore {
    
    @Column(name="store_id")
    @Id
    private Long storeId;
    
    @Column(name="store_name")
    private String storeName;
    
    @Column(name="store_des")
    private String storeDes;
    
    @Column(name="provider_id")
    private Long providerId;
    
    @Column(name="created_at")
    private LocalDateTime createdAt;
    
    @Column(name="updated_at")
    private LocalDateTime updatedAt;
}
