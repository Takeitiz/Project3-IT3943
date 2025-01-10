package com.project3.gatewayserver.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class OrderNotification {
    private String id;
    private String userTo = "";
    private String senderUsername = "";
    private String senderPicture = "";
    private String receiverUsername = "";
    private String receiverPicture = "";
    private Boolean isRead = false;
    private String message = "";
    private String orderId = "";
    private String createdAt;
}
