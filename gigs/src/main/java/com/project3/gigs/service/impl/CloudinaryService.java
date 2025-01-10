package com.project3.gigs.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.project3.gigs.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
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

            Map<String, Object> params = new HashMap<>();
            params.put("resource_type", "auto");
            if (publicId != null) {
                params.put("public_id", publicId);
            }
            if (overwrite != null) {
                params.put("overwrite", overwrite);
            }
            if (invalidate != null) {
                params.put("invalidate", invalidate);
            }

            return cloudinary.uploader().upload(file, params);
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
