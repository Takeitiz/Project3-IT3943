package com.project3.auth.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public Map upload(String file, String publicId, Boolean overwrite, Boolean invalidate) {
        try {
            return cloudinary.uploader().upload(file, ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", overwrite,
                    "invalidate", invalidate,
                    "resource_type", "auto"
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    public Map uploadVideo(String file, String publicId, Boolean overwrite, Boolean invalidate) {
        try {
            return cloudinary.uploader().upload(file, ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", overwrite,
                    "invalidate", invalidate,
                    "chunk_size", 50000,
                    "resource_type", "video"
            ));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload video to Cloudinary", e);
        }
    }
}
