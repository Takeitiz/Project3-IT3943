package com.project3.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SigninDto {

    @NotBlank(message = "Username/Email is a required field")
    private String username;

    @NotBlank(message = "Password is a required field")
    @Size(min = 4, max = 12, message = "Invalid password")
    private String password;

    private String browserName;
    private String deviceType;
}