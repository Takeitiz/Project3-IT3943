package com.project3.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderMessageDto {
    private String sellerId;
    private String buyerId;
    private Integer ongoingJobs;
    private Integer completedJobs;
    private Double totalEarnings;
    private String purchasedGigs;
    private String recentDelivery;
    private String type;
    private String receiverEmail;
    private String username;
    private String template;
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
    private LocalDateTime orderDue;
    private String requirements;
    private String orderUrl;
    private LocalDateTime originalDate;
    private LocalDateTime newDate;
    private String reason;
    private String subject;
    private String header;
    private Double total;
    private String message;
    private Double serviceFee;
}
