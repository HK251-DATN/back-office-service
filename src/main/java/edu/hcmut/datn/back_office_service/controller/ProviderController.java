package edu.hcmut.datn.back_office_service.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.VerificationMethod;
import edu.hcmut.datn.back_office_service.common.enums.VerificationStatus;
import edu.hcmut.datn.back_office_service.dao.Provider;
import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;
import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;
import edu.hcmut.datn.back_office_service.dto.request.ProviderCreateRequest;
import edu.hcmut.datn.back_office_service.dto.request.ProviderUpdateRequest;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.dto.response.ProviderDetailResponse;
import edu.hcmut.datn.back_office_service.dto.response.ProviderVerificationStatusResponse;
import edu.hcmut.datn.back_office_service.exception.provider.ProviderNotFoundException;
import edu.hcmut.datn.back_office_service.security.portable.AuthenticatedUser;
import edu.hcmut.datn.back_office_service.service.ProviderCertificateService;
import edu.hcmut.datn.back_office_service.service.ProviderService;
import edu.hcmut.datn.back_office_service.service.ProviderVerificationVideoService;
import edu.hcmut.datn.back_office_service.service.UserService;

/**
 * REST controller for provider management.
 *
 * <p>Exposes two groups of endpoints:
 * <ul>
 *   <li><b>Provider self-service</b> ({@code /me}, {@code /my-status}, {@code /upload-logo}) —
 *       operate on the authenticated provider's own account.</li>
 *   <li><b>Admin</b> (all other paths) — manage any provider, their certificates, and videos.</li>
 * </ul>
 */
@Controller
@RequestMapping("/api/provider")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;
    private final ProviderCertificateService certificateService;
    private final ProviderVerificationVideoService videoService;
    private final UserService userService;

    /**
     * Returns the merged provider + user profile for the currently authenticated provider.
     *
     * <p>Returns {@code SKIP_AS_GOOD} when the authenticated user has no provider account.
     *
     * @param principal the authenticated user extracted from the JWT
     * @return {@link ProviderDetailResponse} containing both provider and user fields
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProviderDetailResponse>> getMyInformation(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        try {
            Provider provider = providerService.readByUserId(principal.getId());
            ProviderDetailResponse detail = ProviderDetailResponse.from(
                    provider, userService.read(provider.getUserId()));
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get provider information successfully", detail));
        } catch (ProviderNotFoundException e) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "This account is not a provider", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Returns the verification status of the currently authenticated provider,
     * including all submitted certificates and videos.
     *
     * @param principal the authenticated user extracted from the JWT
     * @return {@link ProviderVerificationStatusResponse} with status, certificates, and videos
     */
    @GetMapping("/my-status")
    public ResponseEntity<ApiResponse<ProviderVerificationStatusResponse>> getMyVerificationStatus(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        try {
            Provider provider = providerService.readByUserId(principal.getId());

            ProviderVerificationStatusResponse response = ProviderVerificationStatusResponse.builder()
                    .providerId(provider.getProviderId())
                    .verificationStatus(provider.getVerificationStatus())
                    .verificationMethod(provider.getVerificationMethod())
                    .certificates(certificateService.readAllByProvider(principal.getId()))
                    .videos(videoService.readAllByProvider(principal.getId()))
                    .build();

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get verification status successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Creates a new provider record.
     *
     * @param providerCreateRequest provider fields to persist
     * @return the newly created {@link Provider}
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Provider>> create(@RequestBody ProviderCreateRequest providerCreateRequest) {
        try {
            Provider newProvider = providerService.create(providerCreateRequest.toEntity());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Create provider successfully", newProvider));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Returns the merged provider + user profile for the given provider (admin).
     *
     * @param providerId the provider's primary key
     * @return {@link ProviderDetailResponse} containing both provider and user fields
     */
    @GetMapping("/{providerId}")
    public ResponseEntity<ApiResponse<ProviderDetailResponse>> read(@PathVariable Long providerId) {
        try {
            Provider provider = providerService.read(providerId);
            ProviderDetailResponse detail;

            if (provider.getVerificationMethod() == VerificationMethod.CERTIFICATE) {
                detail = ProviderDetailResponse.from(
                        provider,
                        userService.read(provider.getUserId()),
                        certificateService.readApprovedByProviderId(providerId));
            } else {
                detail = ProviderDetailResponse.from(
                        provider, userService.read(provider.getUserId()));
            }

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Read provider successfully", detail));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Returns a paginated list of providers, optionally filtered by verification status (admin).
     *
     * @param pageNum  1-based page index (default 1)
     * @param pageSize number of records per page (default 20)
     * @param status   optional {@link VerificationStatus} filter
     * @return list of matching {@link Provider} records, or {@code SKIP_AS_GOOD} when empty
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Provider>>> readAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) VerificationStatus status) {
        List<Provider> providers = providerService.readAll(pageNum, pageSize, status);

        if (providers.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No provider found", null));
        }
        return ResponseEntity.ok()
                .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Read all providers successfully", providers));
    }

    /**
     * Returns all food-safety certificates submitted by the given provider (admin).
     *
     * @param providerId the provider's primary key
     * @return list of {@link ProviderCertificate} records, or {@code SKIP_AS_GOOD} when empty
     */
    @GetMapping("/{providerId}/certificates")
    public ResponseEntity<ApiResponse<List<ProviderCertificate>>> adminGetCertificates(
            @PathVariable Long providerId) {
        try {
            List<ProviderCertificate> certificates = certificateService.readAllByProviderId(providerId);

            if (certificates.isEmpty()) {
                return ResponseEntity.ok()
                        .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No certificates found", null));
            }
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get certificates successfully", certificates));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Returns all verification videos submitted by the given provider (admin).
     *
     * @param providerId the provider's primary key
     * @return list of {@link ProviderVerificationVideo} records, or {@code SKIP_AS_GOOD} when empty
     */
    @GetMapping("/{providerId}/videos")
    public ResponseEntity<ApiResponse<List<ProviderVerificationVideo>>> adminGetVideos(
            @PathVariable Long providerId) {
        try {
            List<ProviderVerificationVideo> videos = videoService.readAllByProviderId(providerId);

            if (videos.isEmpty()) {
                return ResponseEntity.ok()
                        .body(ApiResponse.SKIP_AS_GOOD(HttpStatus.OK.toString(), "No verification videos found", null));
            }
            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get videos successfully", videos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Updates provider fields (e.g. {@code verificationStatus}) for the given provider (admin).
     * Null fields in the request body are ignored.
     *
     * @param providerId            the provider's primary key
     * @param providerUpdateRequest fields to update
     * @return the updated {@link Provider}
     */
    @PutMapping("/{providerId}")
    public ResponseEntity<ApiResponse<Provider>> update(@PathVariable Long providerId,
            @RequestBody ProviderUpdateRequest providerUpdateRequest) {
        try {
            Provider updatedProvider = providerService.update(providerId, providerUpdateRequest.toEntity());

            return ResponseEntity.ok().body(
                    ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Update provider successfully", updatedProvider));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Deletes the given provider record (admin).
     *
     * @param providerId the provider's primary key
     */
    @DeleteMapping("{providerId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long providerId) {
        try {
            providerService.delete(providerId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Delete provider successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    /**
     * Uploads or replaces the logo for the currently authenticated provider.
     * The old logo is deleted from R2 before the new one is stored.
     *
     * @param principal the authenticated user extracted from the JWT
     * @param file      the image file to upload
     * @return the updated {@link Provider} with the new {@code logoUrl}
     */
    @PostMapping("/upload-logo")
    public ResponseEntity<ApiResponse<Provider>> uploadLogo(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam("file") MultipartFile file) {
        try {
            Provider provider = providerService.uploadLogo(principal.getId(), file);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Upload logo successfully", provider));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

}
