package com.project3.gatewayserver.dto;

import lombok.Data;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;

@Data
public class Message {

    private String id;

    private String conversationId;

    private String senderUsername;

    private String receiverUsername;

    private String senderPicture;
    private String receiverPicture;
    private String body = "";
    private String file = "";
    private String fileType = "";
    private String fileSize = "";
    private String fileName = "";
    private String gigId = "";
    private String buyerId;
    private String sellerId;
    private boolean isRead = false;
    private boolean hasOffer = false;
    private Offer offer = new Offer();

    private LocalDateTime createdAt;

    @Data
    public static class Offer {
        private String gigTitle = "";
        private Double price = 0.0;
        private String description = "";
        private Integer deliveryInDays = 0;
        private String oldDeliveryDate = "";
        private String newDeliveryDate = "";
        private boolean accepted = false;
        private boolean cancelled = false;
    }
}
