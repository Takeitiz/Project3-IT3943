package com.project3.auth.dto;

import com.project3.auth.entity.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponseDto {
    private String message;
    private Auth user;
    private String token;
}
