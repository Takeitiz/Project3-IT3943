package com.project3.gigs.service.impl;

import com.cloudinary.Cloudinary;
import com.project3.gigs.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public Map uploadFile(MultipartFile file) {
        try {
            return cloudinary.uploader().upload(
                    file.getBytes(),
                    Map.of("resource_type", "auto")
            );
        } catch (IOException e) {
            throw new BadRequestException("File upload error: " + e.getMessage());
        }
    }
}
