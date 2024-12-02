package com.project3.auth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
