package com.project3.notificationv2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailLocalsDto {
    private String sender;
    private String appLink;
    private String appIcon;
    private String offerLink;
    private Double amount;
    private String buyerUsername;
    private String sellerUsername;
    private String title;
    private String description;
    private String deliveryDays;
    private String orderId;
    private LocalDateTime orderDue;
    private String requirements;
    private String orderUrl;
    private LocalDateTime originalDate;
    private LocalDateTime newDate;
    private String reason;
    private String subject;
    private String header;
    private String type;
    private String message;
    private Double serviceFee;
    private Double total;
    private String username;
    private String verifyLink;
    private String resetLink;
    private String otp;
}
