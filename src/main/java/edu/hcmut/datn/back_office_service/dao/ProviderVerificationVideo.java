package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
import edu.hcmut.datn.back_office_service.common.enums.VideoType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "provider_verification_videos")
@NoArgsConstructor
@Getter
public class ProviderVerificationVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "video_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private VideoType videoType;

    @Column(name = "video_url")
    @Setter
    private String videoUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    @Setter
    private String description;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "reviewed_by")
    @Setter
    private Long reviewedBy;

    @Column(name = "review_note", columnDefinition = "TEXT")
    @Setter
    private String reviewNote;

    @Column(name = "reviewed_at")
    @Setter
    private LocalDateTime reviewedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ProviderVerificationVideo(
            Long providerId,
            VideoType videoType,
            String videoUrl,
            String description) {
        this.providerId = providerId;
        this.videoType = videoType;
        this.videoUrl = videoUrl;
        this.description = description;
        this.status = ReviewStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
