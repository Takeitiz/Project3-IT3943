package com.project3.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderEmailMessageDto {
    private String receiverEmail;
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
    private String orderDue;
    private String requirements;
    private String orderUrl;
    private String originalDate;
    private String newDate;
    private String reason;
    private String subject;
    private String header;
    private String type;
    private String message;
    private Double serviceFee;
    private Double total;
}
