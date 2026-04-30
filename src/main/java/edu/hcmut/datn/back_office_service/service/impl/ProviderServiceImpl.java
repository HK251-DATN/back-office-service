package edu.hcmut.datn.back_office_service.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;
import edu.hcmut.datn.back_office_service.exception.provider.ProviderAlreadyExistsException;
import edu.hcmut.datn.back_office_service.exception.provider.ProviderNotFoundException;
import edu.hcmut.datn.back_office_service.messaging.provider.ProviderVerificationProducer;
import edu.hcmut.datn.back_office_service.messaging.provider.ProviderVerificationUpdatedEvent;
import edu.hcmut.datn.back_office_service.repository.ProviderCertificateRepository;
import edu.hcmut.datn.back_office_service.repository.ProviderRepository;
import edu.hcmut.datn.back_office_service.service.ProviderService;
import edu.hcmut.datn.back_office_service.service.R2UploadService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderCertificateRepository certificateRepository;
    private final ProviderVerificationProducer verificationProducer;
    private final R2UploadService r2UploadService;

    @Value("${app.provider-logo-bucket}")
    private String logoBucket;

    public ProviderServiceImpl(ProviderRepository providerRepository,
                              ProviderCertificateRepository certificateRepository,
                              ProviderVerificationProducer verificationProducer,
                              R2UploadService r2UploadService) {
        this.providerRepository = providerRepository;
        this.certificateRepository = certificateRepository;
        this.verificationProducer = verificationProducer;
        this.r2UploadService = r2UploadService;
    }

    @Override
    public Provider create(Provider provider) {
        if (Boolean.TRUE.equals(providerRepository.existsByUserId(provider.getUserId()))) {
            throw new ProviderAlreadyExistsException("This user id has already linked with one provider account");
        }

        return providerRepository.save(provider);
    }

    @Override
    public Provider read(Long providerId) {
        return providerRepository.findById(providerId).orElseThrow(() -> new ProviderNotFoundException("Provider not found"));
    }

    @Override
    public Provider readByUserId(Long userId) {
        return providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ProviderNotFoundException("No provider account found for this user"));
    }

    @Override
    public List<Provider> readAll(Integer pageNum, Integer pageSize, VerificationStatus status) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);

        if (status != null) {
            return providerRepository.findAllByVerificationStatus(status, pageable);
        }

        return providerRepository.findAll(pageable).toList();
    }

    @Override
    public Provider update(Long providerId, Provider provider) {
        Provider curProvider = read(providerId);
        boolean verificationChanged = false;

        if (provider.getReputationPoint() != null) {
            curProvider.setReputationPoint(provider.getReputationPoint());
        }

        if (provider.getVerificationStatus() != null) {
            curProvider.setVerificationStatus(provider.getVerificationStatus());
            verificationChanged = true;
        }

        if (provider.getVerificationMethod() != null) {
            curProvider.setVerificationMethod(provider.getVerificationMethod());
            verificationChanged = true;
        }

        if (provider.getBankId() != null) {
            curProvider.setBankId(provider.getBankId());
        }

        if (provider.getBankNum() != null) {
            curProvider.setBankNum(provider.getBankNum());
        }

        Provider savedProvider = providerRepository.save(curProvider);

        // Publish verification update event if verification fields changed
        if (verificationChanged) {
            publishVerificationUpdate(savedProvider);
        }

        return savedProvider;
    }

    private void publishVerificationUpdate(Provider provider) {
        try {
            // Get the first approved certificate (if any) for CERTIFICATE method
            var certificateType = provider.getVerificationMethod() != null
                    && provider.getVerificationMethod().name().equals("CERTIFICATE")
                    ? certificateRepository.findAllByProviderId(provider.getProviderId())
                            .stream()
                            .filter(cert -> cert.getStatus() != null
                                    && cert.getStatus().name().equals("APPROVED"))
                            .findFirst()
                            .map(ProviderCertificate::getCertificateType)
                            .orElse(null)
                    : null;

            ProviderVerificationUpdatedEvent event = new ProviderVerificationUpdatedEvent(
                    provider.getProviderId(),
                    provider.getVerificationStatus(),
                    provider.getVerificationMethod(),
                    certificateType,
                    provider.getLogoUrl()
            );
            verificationProducer.publishVerificationUpdated(event);
        } catch (Exception e) {
            log.error("Failed to publish verification update for provider {}: {}",
                    provider.getProviderId(), e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long providerId) {
        Provider curProvider = read(providerId);

        providerRepository.delete(curProvider);
    }

    @Override
    public Provider uploadLogo(Long userId, MultipartFile file) {
        Provider provider = readByUserId(userId);

        // Delete old logo if exists
        if (provider.getLogoUrl() != null && !provider.getLogoUrl().isEmpty()) {
            String oldKey = extractKeyFromUrl(provider.getLogoUrl());
            try {
                r2UploadService.delete(oldKey, logoBucket);
            } catch (Exception e) {
                log.warn("Failed to delete old logo from R2, key={}: {}", oldKey, e.getMessage());
            }
        }

        // Upload new logo
        String logoUrl = r2UploadService.upload(file, logoBucket);
        provider.setLogoUrl(logoUrl);

        Provider savedProvider = providerRepository.save(provider);

        // Publish logo update event
        publishVerificationUpdate(savedProvider);

        return savedProvider;
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
