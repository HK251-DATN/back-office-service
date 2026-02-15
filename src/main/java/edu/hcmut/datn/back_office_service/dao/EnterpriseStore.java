package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
public class EnterpriseStore {

    @Column(name="store_id")
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Getter
    private Long storeId;

    @Column(name="store_name")
    @Getter
    @Setter
    private String storeName;

    @Column(name="store_des")
    @Getter
    @Setter
    private String storeDes;

    @Column(name="provider_id")
    @Getter
    private Long providerId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    protected EnterpriseStore() {}

    // For creating purpose
    public EnterpriseStore(String storeName, String storeDes, Long providerId) {
        this.storeName = storeName;
        this.storeDes = storeDes;
        this.providerId = providerId;
    }

    // For updating purpose
    public EnterpriseStore(String storeName, String storeDes) {
        this.storeName = storeName;
        this.storeDes = storeDes;
    }
}
