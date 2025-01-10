package com.project3.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MarkMultipleMessageDto {
    private String messageId;
    private String senderUsername;
    private String receiverUsername;
}
