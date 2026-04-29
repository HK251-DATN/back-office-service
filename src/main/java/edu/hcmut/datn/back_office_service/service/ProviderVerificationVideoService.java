package edu.hcmut.datn.back_office_service.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
import edu.hcmut.datn.back_office_service.common.enums.VideoType;
import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;

public interface ProviderVerificationVideoService {

    ProviderVerificationVideo create(Long userId, VideoType videoType, String description);

    ProviderVerificationVideo uploadFile(Long videoId, MultipartFile file);

    ProviderVerificationVideo read(Long videoId);

    List<ProviderVerificationVideo> readAllByProvider(Long userId);

    List<ProviderVerificationVideo> readAllByProviderId(Long providerId);

    ProviderVerificationVideo review(Long videoId, ReviewStatus status, String reviewNote, Long reviewedBy);

    void delete(Long videoId);
}
