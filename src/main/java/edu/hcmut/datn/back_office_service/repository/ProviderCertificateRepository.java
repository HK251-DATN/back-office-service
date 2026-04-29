package edu.hcmut.datn.back_office_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;

public interface ProviderCertificateRepository extends JpaRepository<ProviderCertificate, Long> {

    List<ProviderCertificate> findAllByProviderId(Long providerId);
}
