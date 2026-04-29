package edu.hcmut.datn.back_office_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.dao.Provider;

public interface ProviderRepository extends JpaRepository<Provider, Long>{

    Boolean existsByUserId(Long userId);

    Optional<Provider> findByUserId(Long userId);

    List<Provider> findAllByVerificationStatus(VerificationStatus verificationStatus, Pageable pageable);
}
