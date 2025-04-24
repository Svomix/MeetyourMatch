package com.javanostra.spring.core.services;

import com.javanostra.spring.core.exceptions.FileUploadFailedException;
import com.javanostra.spring.core.exceptions.FileServiceException;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@AllArgsConstructor
@Service
public class FileService {
    private MinioClient minioClient;

    static public String STATIC_PREFIX = "/static/";
    static public String AVATAR_PREFIX = "avatars";
    static public String CONTENT_PREFIX = "avatars";

    public String getPath(String bucket, String object_id) {
        return STATIC_PREFIX + bucket + "/" + object_id;
    }

    public void uploadFile(String bucket, String name, InputStream inputStream, String contentType) throws FileUploadFailedException {
        try {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(name).stream(inputStream, -1, 1024 * 1024 * 16).contentType(contentType).build());
        }catch (Exception e){
            throw new FileUploadFailedException(e.getClass().getSimpleName() + ":" + e.getMessage());
        }
    }

    public Boolean fileExists(String bucket, String name) throws FileServiceException {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(bucket).object(name).build());
            return true;
        }catch (ErrorResponseException e){
            if(e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new FileServiceException(e.getClass().getSimpleName() + ":" + e.getMessage());
        }catch (Exception e){
            throw new FileServiceException(e.getClass().getSimpleName() + ":" + e.getMessage());
        }
    }
}
