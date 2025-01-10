package com.project3.users.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuthBuyerMessageDetailsDto {

    private String username;
    private String profilePicture;
    private String email;
    private String country;
    private LocalDateTime createdAt;
    private String type;
}
