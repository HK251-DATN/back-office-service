package edu.hcmut.datn.back_office_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
import edu.hcmut.datn.back_office_service.common.enums.VerificationMethod;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.common.enums.VideoType;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;
import edu.hcmut.datn.back_office_service.exception.provider.ProviderNotFoundException;
import edu.hcmut.datn.back_office_service.exception.providerverificationvideo.ProviderVerificationVideoNotFoundException;
import edu.hcmut.datn.back_office_service.repository.ProviderRepository;
import edu.hcmut.datn.back_office_service.repository.ProviderVerificationVideoRepository;
import edu.hcmut.datn.back_office_service.service.ProviderService;
import edu.hcmut.datn.back_office_service.service.ProviderVerificationVideoService;
import edu.hcmut.datn.back_office_service.service.R2UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderVerificationVideoServiceImpl implements ProviderVerificationVideoService {

    private final ProviderVerificationVideoRepository videoRepository;
    private final ProviderRepository providerRepository;
    private final ProviderService providerService;
    private final R2UploadService r2UploadService;

    @Value("${app.provider-video-bucket}")
    private String videoBucket;

    private Provider findProviderByUserId(Long userId) {
        return providerRepository.findByUserId(userId)
                .orElseThrow(() -> new ProviderNotFoundException("No provider account found for this user"));
    }

    @Override
    public ProviderVerificationVideo create(Long userId, VideoType videoType, String description) {
        Provider provider = findProviderByUserId(userId);

        ProviderVerificationVideo video = new ProviderVerificationVideo(
                provider.getProviderId(), videoType, null, description);

        return videoRepository.save(video);
    }

    @Override
    public ProviderVerificationVideo createByUrl(Long userId, VideoType videoType, String description, String videoUrl) {
        Provider provider = findProviderByUserId(userId);

        ProviderVerificationVideo video = new ProviderVerificationVideo(
                provider.getProviderId(), videoType, videoUrl, description);

        ProviderVerificationVideo saved = videoRepository.save(video);

        // Set provider verification status to PENDING, same as uploadFile()
        provider.setVerificationStatus(VerificationStatus.PENDING);
        providerRepository.save(provider);

        return saved;
    }

    @Override
    public ProviderVerificationVideo uploadFile(Long videoId, MultipartFile file) {
        ProviderVerificationVideo video = read(videoId);

        if (video.getVideoUrl() != null) {
            String oldKey = extractKeyFromUrl(video.getVideoUrl());
            try {
                r2UploadService.delete(oldKey, videoBucket);
            } catch (Exception e) {
                log.warn("Failed to delete old video from R2, key={}: {}", oldKey, e.getMessage());
            }
        }

        String videoUrl = r2UploadService.upload(file, videoBucket);
        video.setVideoUrl(videoUrl);

        ProviderVerificationVideo saved = videoRepository.save(video);

        // Set provider verification status to PENDING after video upload
        Provider provider = providerRepository.findById(video.getProviderId())
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found"));
        provider.setVerificationStatus(VerificationStatus.PENDING);
        providerRepository.save(provider);

        return saved;
    }

    @Override
    public ProviderVerificationVideo read(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ProviderVerificationVideoNotFoundException("Verification video not found: " + videoId));
    }

    @Override
    public List<ProviderVerificationVideo> readAllByProvider(Long userId) {
        Provider provider = findProviderByUserId(userId);
        return videoRepository.findAllByProviderId(provider.getProviderId());
    }

    @Override
    public List<ProviderVerificationVideo> readAllByProviderId(Long providerId) {
        return videoRepository.findAllByProviderId(providerId);
    }

    @Override
    public ProviderVerificationVideo review(Long videoId, ReviewStatus status, String reviewNote, Long reviewedBy) {
        ProviderVerificationVideo video = read(videoId);

        video.setStatus(status);
        video.setReviewNote(reviewNote);
        video.setReviewedBy(reviewedBy);
        video.setReviewedAt(LocalDateTime.now());

        ProviderVerificationVideo saved = videoRepository.save(video);

        if (status == ReviewStatus.APPROVED) {
            Provider current = providerRepository.findById(video.getProviderId())
                    .orElseThrow(() -> new ProviderNotFoundException("Provider not found: " + video.getProviderId()));

            Provider patch = new Provider();
            patch.setVerificationStatus(VerificationStatus.APPROVED);
            // Only set to VIDEO if not already CERTIFICATE (which is higher priority)
            if (current.getVerificationMethod() != VerificationMethod.CERTIFICATE) {
                patch.setVerificationMethod(VerificationMethod.VIDEO);
            }
            providerService.update(video.getProviderId(), patch);
        } else if (status == ReviewStatus.REJECTED) {
            Provider patch = new Provider();
            patch.setVerificationStatus(VerificationStatus.REJECTED);
            providerService.update(video.getProviderId(), patch);
        }

        return saved;
    }

    @Override
    public void delete(Long videoId) {
        ProviderVerificationVideo video = read(videoId);

        if (video.getVideoUrl() != null) {
            String key = extractKeyFromUrl(video.getVideoUrl());
            try {
                r2UploadService.delete(key, videoBucket);
            } catch (Exception e) {
                log.warn("Failed to delete verification video from R2, key={}: {}", key, e.getMessage());
            }
        }

        videoRepository.delete(video);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}
