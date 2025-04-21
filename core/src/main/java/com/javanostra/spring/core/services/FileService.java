package com.javanostra.spring.core.services;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class FileService {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access_key}")
    private String accessKey;
    @Value("${minio.secret_key}")
    private String secretKey;

    static public String STATIC_PREFIX = "/static/";
    static public String AVATAR_PREFIX = "avatars";

    public void uploadFile(String bucket, String name, InputStream inputStream, String contentType) {
        try {
            MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(name).stream(inputStream, -1, 1024*1024*16).contentType(contentType).build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
