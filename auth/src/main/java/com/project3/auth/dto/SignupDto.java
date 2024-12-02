package com.project3.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SignupDto {

    @NotBlank(message = "Username is a required field")
    @Size(min = 4, max = 12, message = "Invalid username")
    private String username;

    @NotBlank(message = "Password is a required field")
    @Size(min = 4, max = 12, message = "Invalid password")
    private String password;

    @NotBlank(message = "Country is a required field")
    private String country;

    @NotBlank(message = "Email is a required field")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Profile picture is required")
    private String profilePicture;

    private String browserName;
    private String deviceType;
}
