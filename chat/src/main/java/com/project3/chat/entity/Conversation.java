package com.project3.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "conversation")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conversation {

    @Id
    @Indexed
    private String conversationId;

    @Indexed
    private String senderUsername;

    @Indexed
    private String receiverUsername;
}
