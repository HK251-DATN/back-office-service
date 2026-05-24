package edu.hcmut.datn.back_office_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.CertificateType;
import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;

public interface ProviderCertificateService {

    ProviderCertificate upload(Long userId, CertificateType certificateType, String certificateNumber,
            String issuingAuthority, LocalDate issuedDate, LocalDate expiryDate, MultipartFile file);

    ProviderCertificate read(Long certificateId);

    List<ProviderCertificate> readAllByProvider(Long userId);

    List<ProviderCertificate> readAllByProviderId(Long providerId);

    List<ProviderCertificate> readApprovedByProviderId(Long providerId);

    ProviderCertificate review(Long certificateId, ReviewStatus status, String reviewNote, Long reviewedBy);

    void delete(Long certificateId);
}
