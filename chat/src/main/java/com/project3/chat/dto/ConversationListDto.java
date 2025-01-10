package com.project3.chat.dto;

import com.project3.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationListDto {
    private String message;
    private List<Message> conversations;
}
