package edu.hcmut.datn.back_office_service.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
import edu.hcmut.datn.back_office_service.common.enums.VideoType;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;
import edu.hcmut.datn.back_office_service.exception.provider.ProviderNotFoundException;
import edu.hcmut.datn.back_office_service.exception.providerverificationvideo.ProviderVerificationVideoNotFoundException;
import edu.hcmut.datn.back_office_service.repository.ProviderRepository;
import edu.hcmut.datn.back_office_service.repository.ProviderVerificationVideoRepository;
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

        return videoRepository.save(video);
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

        return videoRepository.save(video);
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
