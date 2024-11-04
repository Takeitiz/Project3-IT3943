package com.project3.chat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "conversation")
public class Conversation {

    @Id
    @Indexed
    private String conversationId;

    @Indexed
    private String senderUsername;

    @Indexed
    private String receiverUsername;
}
