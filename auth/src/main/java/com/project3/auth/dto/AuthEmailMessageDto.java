package com.project3.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthEmailMessageDto {
    private String receiverEmail;
    private String username;
    private String verifyLink;
    private String resetLink;
    private String templateName;
    private String subject;
    private String otp;
}
