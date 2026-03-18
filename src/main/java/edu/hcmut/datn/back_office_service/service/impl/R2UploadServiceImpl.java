package edu.hcmut.datn.back_office_service.service.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.hcmut.datn.back_office_service.service.R2UploadService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class R2UploadServiceImpl implements R2UploadService {

    private final S3Client s3Client;

    @Value("${cloudflare.r2.account-id}")
    private String accountId;

    @Value("${app.user-avatar-public-bucket-url}")
    private String bucketPublicUrl;

    @Override
    public String upload(MultipartFile file, String bucket) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return buildFileUrl(fileName, bucket);
        } catch (Exception e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

    private String buildFileUrl(String key, String bucket) {
        String buckerPublicUrl = getBucketPublicUrl(bucket);

        return bucketPublicUrl + "/" + key;
    }

    private String getBucketPublicUrl(String bucket) {
        String publicUrl = "";
        switch (bucket) {
            case "back-office-user-avts" ->
                publicUrl = bucketPublicUrl;
            default ->
                throw new AssertionError();
        }
        return publicUrl;
    }
}
