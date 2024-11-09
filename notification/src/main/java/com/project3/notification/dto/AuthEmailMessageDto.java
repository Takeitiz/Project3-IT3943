package com.project3.notification.dto;

import lombok.Data;

@Data
public class AuthEmailMessageDto {
    private String receiverEmail;
    private String username;
    private String verifyLink;
    private String resetLink;
    private String templateName;
    private String subject;
    private String otp;
}
