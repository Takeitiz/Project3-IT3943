package com.project3.gatewayserver.dto;

import lombok.Data;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;

@Data
public class Notification {

    private String id;

    private String userTo = "";

    private String senderUsername = "";

    private String senderPicture = "";

    private String receiverUsername = "";

    private String receiverPicture = "";

    private Boolean isRead = false;

    private String message = "";

    private String orderId = "";

    private LocalDateTime createdAt;
}
