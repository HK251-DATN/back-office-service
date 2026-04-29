package edu.hcmut.datn.back_office_service.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.CertificateType;
import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;
import edu.hcmut.datn.back_office_service.exception.providercertificate.ProviderCertificateNotFoundException;
import edu.hcmut.datn.back_office_service.exception.provider.ProviderNotFoundException;
import edu.hcmut.datn.back_office_service.repository.ProviderCertificateRepository;
import edu.hcmut.datn.back_office_service.repository.ProviderRepository;
import edu.hcmut.datn.back_office_service.service.ProviderCertificateService;
import edu.hcmut.datn.back_office_service.service.R2UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderCertificateServiceImpl implements ProviderCertificateService {

    private final ProviderCertificateRepository certificateRepository;
    private final ProviderRepository providerRepository;
    private final R2UploadService r2UploadService;

    @Value("${app.provider-cert-bucket}")
    private String certBucket;

    private Provider findProviderByUserId(Long userId) {
        return providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ProviderNotFoundException("No provider account found for this user"));
    }

    @Override
    public ProviderCertificate upload(Long userId, CertificateType certificateType, String certificateNumber,
            String issuingAuthority, LocalDate issuedDate, LocalDate expiryDate, MultipartFile file) {
        Provider provider = findProviderByUserId(userId);

        String documentUrl = r2UploadService.upload(file, certBucket);

        ProviderCertificate certificate = new ProviderCertificate(
                provider.getProviderId(), certificateType, certificateNumber,
                issuingAuthority, issuedDate, expiryDate, documentUrl);

        return certificateRepository.save(certificate);
    }

    @Override
    public ProviderCertificate read(Long certificateId) {
        return certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ProviderCertificateNotFoundException("Certificate not found: " + certificateId));
    }

    @Override
    public List<ProviderCertificate> readAllByProvider(Long userId) {
        Provider provider = findProviderByUserId(userId);
        return certificateRepository.findAllByProviderId(provider.getProviderId());
    }

    @Override
    public List<ProviderCertificate> readAllByProviderId(Long providerId) {
        return certificateRepository.findAllByProviderId(providerId);
    }

    @Override
    public ProviderCertificate review(Long certificateId, ReviewStatus status, String reviewNote, Long reviewedBy) {
        ProviderCertificate certificate = read(certificateId);

        certificate.setStatus(status);
        certificate.setReviewNote(reviewNote);
        certificate.setReviewedBy(reviewedBy);
        certificate.setReviewedAt(LocalDateTime.now());

        return certificateRepository.save(certificate);
    }

    @Override
    public void delete(Long certificateId) {
        ProviderCertificate certificate = read(certificateId);

        if (certificate.getDocumentUrl() != null) {
            String key = extractKeyFromUrl(certificate.getDocumentUrl());
            try {
                r2UploadService.delete(key, certBucket);
            } catch (Exception e) {
                log.warn("Failed to delete certificate document from R2, key={}: {}", key, e.getMessage());
            }
        }

        certificateRepository.delete(certificate);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
