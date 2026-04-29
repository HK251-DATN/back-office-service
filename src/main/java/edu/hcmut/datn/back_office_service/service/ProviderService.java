package edu.hcmut.datn.back_office_service.service;

import java.util.List;

import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.dao.Provider;

public interface ProviderService {
    Provider create(Provider provider);

    Provider read(Long providerId);

    Provider readByUserId(Long userId);

    List<Provider> readAll(Integer pageNum, Integer pageSize, VerificationStatus status);

    Provider update(Long providerId, Provider provider);

    void delete(Long providerId);
}
