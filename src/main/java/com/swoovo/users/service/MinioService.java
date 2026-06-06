package com.swoovo.users.service;

import com.swoovo.users.exception.FileStorageException;
import com.swoovo.users.service.helpers.MinioAction;
import com.swoovo.users.service.helpers.MinioNonResultAction;
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

    public Iterable<Result<Item>> listFiles(String bucketName) throws FileStorageException{
        return executeMinioAction(minioClient::listObjects,
                ListObjectsArgs.builder()
                .bucket(bucketName)
                .build());
    }

    public String downloadFile(String fileName) throws FileStorageException {
        return executeMinioAction(minioClient::getPresignedObjectUrl,
                GetPresignedObjectUrlArgs.builder()
                        .method(Http.Method.GET)
                        .bucket(bucketName)
                        .expiry(minioExpiration)
                        .object(fileName)
                        .build()
        );
    }

    public void uploadFile(String objectName, InputStream stream, long size, String contentType)
            throws FileStorageException {
        createBucketIfNotExists();

        executeMinioAction(minioClient::putObject, PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, size, -1L)
                .contentType(contentType)
                .build());
    }

    private void createBucketIfNotExists() throws FileStorageException {
        boolean found = executeMinioAction(minioClient::bucketExists, BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .build()
        );

        if (!found)
            executeMinioNonResultAction(minioClient::makeBucket, MakeBucketArgs
                    .builder().bucket(bucketName).build());
    }

    private <T, R> R executeMinioAction(MinioAction<T, R> minioAction, T arg) throws FileStorageException {
        try {
            return minioAction.execute(arg);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }

    private <T> void executeMinioNonResultAction(MinioNonResultAction<T> minioNonResultAction,
                                                 T arg) throws FileStorageException {
        try {
            minioNonResultAction.execute(arg);
        } catch (Exception e) {
            throw new FileStorageException(e.getMessage());
        }
    }
}
