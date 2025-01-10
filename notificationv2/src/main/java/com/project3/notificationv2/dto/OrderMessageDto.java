package com.project3.notificationv2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageDto {
    private String sellerId;
    private String buyerId;
    private Integer ongoingJobs;
    private Integer completedJobs;
    private Double totalEarnings;
    private String purchasedGigs;
    private String recentDelivery;
    private String type;
    private String sellerEmail;
    private String buyerEmail;
    private String username;
    private String templateName;
    private String templateSubject;
    private String sender;
    private String offerLink;
    private Double amount;
    private String buyerUsername;
    private String sellerUsername;
    private String title;
    private String description;
    private String deliveryDays;
    private String orderId;
    private String invoiceId;
    private String orderDue;
    private String requirements;
    private String orderUrl;
    private String originalDate;
    private String newDate;
    private String reason;
    private String subject;
    private String header;
    private Double total;
    private String message;
    private Double serviceFee;
    private String declineReason;
}
