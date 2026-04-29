package edu.hcmut.datn.back_office_service.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

import edu.hcmut.datn.back_office_service.common.enums.CertificateType;
import edu.hcmut.datn.back_office_service.common.enums.ReviewStatus;
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
@Table(name = "provider_certificates")
@NoArgsConstructor
@Getter
public class ProviderCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "certificate_id")
    private Long certificateId;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "certificate_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private CertificateType certificateType;

    @Column(name = "certificate_number")
    @Setter
    private String certificateNumber;

    @Column(name = "issuing_authority")
    @Setter
    private String issuingAuthority;

    @Column(name = "issued_date")
    @Setter
    private LocalDate issuedDate;

    @Column(name = "expiry_date")
    @Setter
    private LocalDate expiryDate;

    @Column(name = "document_url")
    @Setter
    private String documentUrl;

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

    public ProviderCertificate(
            Long providerId,
            CertificateType certificateType,
            String certificateNumber,
            String issuingAuthority,
            LocalDate issuedDate,
            LocalDate expiryDate,
            String documentUrl) {
        this.providerId = providerId;
        this.certificateType = certificateType;
        this.certificateNumber = certificateNumber;
        this.issuingAuthority = issuingAuthority;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.documentUrl = documentUrl;
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
