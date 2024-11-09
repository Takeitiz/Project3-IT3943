package com.project3.order.dto;

import lombok.Data;

import java.math.BigDecimal;

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
    private String amount;
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
    private String total;
    private String message;
    private String serviceFee;
}
