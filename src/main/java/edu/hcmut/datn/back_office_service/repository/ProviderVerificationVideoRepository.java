package edu.hcmut.datn.back_office_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;

public interface ProviderVerificationVideoRepository extends JpaRepository<ProviderVerificationVideo, Long> {

    List<ProviderVerificationVideo> findAllByProviderId(Long providerId);
}
