package com.project3.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message")
@Builder
public class Message {
    @Id
    private String id;
    @Indexed
    private String conversationId;
    @Indexed
    private String senderUsername;
    @Indexed
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
