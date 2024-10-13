package com.springcloud.demo.bookingreceipt.images;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service implements ImageProviderService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(byte[] file, String key) {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("application/pdf")
                .build();

        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromBytes(file));

        if (response.sdkHttpResponse().isSuccessful()) {
            return key;
        }

        log.error("Error uploading file");
        response.sdkHttpResponse().statusText().ifPresent(log::error);
        return null;
    }
}
