package com.project3.order.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = "notification")
public class Notification {
    @Id
    private String id;

    @Indexed
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
