package com.project3.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordDto {
    @NotBlank(message = "Password is a required field")
    @Size(min = 4, max = 12, message = "Invalid password")
    private String password;

    @NotBlank(message = "Confirm password is a required field")
    private String confirmPassword;
}
