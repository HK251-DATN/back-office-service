package edu.hcmut.datn.back_office_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.common.enums.CertificateType;
import edu.hcmut.datn.back_office_service.dao.ProviderCertificate;
import edu.hcmut.datn.back_office_service.dto.request.ProviderCertificateReviewRequest;
import edu.hcmut.datn.back_office_service.dto.request.ProviderCertificateUrlUploadRequest;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.security.portable.AuthenticatedUser;
import edu.hcmut.datn.back_office_service.service.ProviderCertificateService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/provider/certificates")
@RequiredArgsConstructor
public class ProviderCertificateController {

    private final ProviderCertificateService certificateService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderCertificate>> upload(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestParam CertificateType certificateType,
            @RequestParam String certificateNumber,
            @RequestParam String issuingAuthority,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate issuedDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate,
            @RequestParam("file") MultipartFile file) {
        try {
            ProviderCertificate certificate = certificateService.upload(
                    principal.getId(), certificateType, certificateNumber,
                    issuingAuthority, issuedDate, expiryDate, file);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Certificate uploaded successfully", certificate));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PostMapping("/url")
    public ResponseEntity<ApiResponse<ProviderCertificate>> uploadByUrl(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody ProviderCertificateUrlUploadRequest request) {
        try {
            ProviderCertificate certificate = certificateService.uploadByUrl(
                    principal.getId(), request.getCertificateType(), request.getCertificateNumber(),
                    request.getIssuingAuthority(), request.getIssuedDate(), request.getExpiryDate(),
                    request.getDocumentUrl());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Certificate registered successfully", certificate));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderCertificate>>> readAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        try {
            List<ProviderCertificate> certificates = certificateService.readAllByProvider(principal.getId());

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

    @GetMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<ProviderCertificate>> read(@PathVariable Long certificateId) {
        try {
            ProviderCertificate certificate = certificateService.read(certificateId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get certificate successfully", certificate));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{certificateId}/review")
    public ResponseEntity<ApiResponse<ProviderCertificate>> review(
            @PathVariable Long certificateId,
            @RequestBody ProviderCertificateReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUser reviewer) {
        try {
            ProviderCertificate updated = certificateService.review(
                    certificateId, request.getStatus(), request.getReviewNote(), reviewer.getId());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Certificate reviewed successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long certificateId) {
        try {
            certificateService.delete(certificateId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Certificate deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }
}
