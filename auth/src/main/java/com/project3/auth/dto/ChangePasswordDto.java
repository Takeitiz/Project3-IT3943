package com.project3.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordDto {

    @NotBlank(message = "Password is a required field")
    @Size(min = 4, max = 8, message = "Invalid password")
    private String currentPassword;

    @NotBlank(message = "Password is a required field")
    @Size(min = 4, max = 12, message = "Invalid password")
    private String newPassword;

    private String username;
}
