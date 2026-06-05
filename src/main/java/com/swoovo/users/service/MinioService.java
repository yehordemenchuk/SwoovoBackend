package com.swoovo.users.service;

import io.minio.*;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {
    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.expiration}")
    private int minioExpiration;

    private final MinioClient minioClient;

    public Iterable<Result<Item>> listFiles(String bucketName) {
        return minioClient.listObjects(
            ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build()
        );
    }

    public String downloadFile(String fileName) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Http.Method.GET)
                        .bucket(bucketName)
                        .expiry(minioExpiration)
                        .object(fileName)
                        .build()
        );
    }

    public void uploadFile(String objectName, InputStream stream, long size, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(stream, size, -1L)
                        .contentType(contentType)
                        .build()
        );
    }

    private void createBucketIfNotExists() throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(bucketName)
                        .build()
        );

        if (!found)
            minioClient.makeBucket(MakeBucketArgs
                    .builder().bucket(bucketName).build());
    }
}
