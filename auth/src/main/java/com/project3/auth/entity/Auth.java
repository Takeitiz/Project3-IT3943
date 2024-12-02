package com.project3.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "auth")
@Builder
public class Auth {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Indexed(unique = true)
    private String profilePublicId;

    @Indexed(unique = true)
    private String email;

    private String country;

    private String profilePicture;

    private String emailVerificationToken;

    private Integer emailVerified = 0;

    private String browserName;

    private String deviceType;

    private String otp;

    private LocalDateTime otpExpiration = LocalDateTime.now();

    private LocalDateTime createdAt = LocalDateTime.now();

    private String passwordResetToken;

    private LocalDateTime passwordResetExpires = LocalDateTime.now();
}
