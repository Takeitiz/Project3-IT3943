package com.project3.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailDto {

    @NotBlank(message = "Field must be valid")
    @Email(message = "Field must be valid")
    private String email;
}