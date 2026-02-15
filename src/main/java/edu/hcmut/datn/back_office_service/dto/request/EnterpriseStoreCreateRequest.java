package edu.hcmut.datn.back_office_service.dto.request;

import edu.hcmut.datn.back_office_service.dao.EnterpriseStore;

public class EnterpriseStoreCreateRequest {

    private String storeName;
    private String storeDes;
    private Long providerId;

    public EnterpriseStore toEntity() {
        return new EnterpriseStore(storeName, storeDes, providerId);
    }
}
