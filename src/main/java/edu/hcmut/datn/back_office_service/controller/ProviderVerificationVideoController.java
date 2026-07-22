package edu.hcmut.datn.back_office_service.controller;

import java.util.List;

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

import edu.hcmut.datn.back_office_service.dao.ProviderVerificationVideo;
import edu.hcmut.datn.back_office_service.dto.request.ProviderVerificationVideoCreateRequest;
import edu.hcmut.datn.back_office_service.dto.request.ProviderVerificationVideoReviewRequest;
import edu.hcmut.datn.back_office_service.dto.request.ProviderVerificationVideoUrlCreateRequest;
import edu.hcmut.datn.back_office_service.dto.response.ApiResponse;
import edu.hcmut.datn.back_office_service.security.portable.AuthenticatedUser;
import edu.hcmut.datn.back_office_service.service.ProviderVerificationVideoService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/provider/videos")
@RequiredArgsConstructor
public class ProviderVerificationVideoController {

    private final ProviderVerificationVideoService videoService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProviderVerificationVideo>> create(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody ProviderVerificationVideoCreateRequest request) {
        try {
            ProviderVerificationVideo video = videoService.create(
                    principal.getId(), request.getVideoType(), request.getDescription());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Video record created successfully", video));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PostMapping("/url")
    public ResponseEntity<ApiResponse<ProviderVerificationVideo>> createByUrl(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @RequestBody ProviderVerificationVideoUrlCreateRequest request) {
        try {
            ProviderVerificationVideo video = videoService.createByUrl(
                    principal.getId(), request.getVideoType(), request.getDescription(), request.getVideoUrl());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Video registered successfully", video));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PostMapping("/{videoId}/upload")
    public ResponseEntity<ApiResponse<ProviderVerificationVideo>> uploadFile(
            @PathVariable Long videoId,
            @RequestParam("file") MultipartFile file) {
        try {
            ProviderVerificationVideo video = videoService.uploadFile(videoId, file);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Video uploaded successfully", video));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProviderVerificationVideo>>> readAll(
            @AuthenticationPrincipal AuthenticatedUser principal) {
        try {
            List<ProviderVerificationVideo> videos = videoService.readAllByProvider(principal.getId());

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

    @GetMapping("/{videoId}")
    public ResponseEntity<ApiResponse<ProviderVerificationVideo>> read(@PathVariable Long videoId) {
        try {
            ProviderVerificationVideo video = videoService.read(videoId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Get video successfully", video));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @PutMapping("/{videoId}/review")
    public ResponseEntity<ApiResponse<ProviderVerificationVideo>> review(
            @PathVariable Long videoId,
            @RequestBody ProviderVerificationVideoReviewRequest request,
            @AuthenticationPrincipal AuthenticatedUser reviewer) {
        try {
            ProviderVerificationVideo updated = videoService.review(
                    videoId, request.getStatus(), request.getReviewNote(), reviewer.getId());

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Video reviewed successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }

    @DeleteMapping("/{videoId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long videoId) {
        try {
            videoService.delete(videoId);

            return ResponseEntity.ok()
                    .body(ApiResponse.SUCCESS(HttpStatus.OK.toString(), "Video deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.ERROR(HttpStatus.BAD_REQUEST.toString(), e.getMessage(), null));
        }
    }
}
