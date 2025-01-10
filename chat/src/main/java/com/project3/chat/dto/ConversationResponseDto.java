package com.project3.chat.dto;

import com.project3.chat.entity.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConversationResponseDto {
    private String message;
    private List<Conversation> conversations;
}
