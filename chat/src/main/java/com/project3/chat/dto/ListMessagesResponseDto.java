package com.project3.chat.dto;

import com.project3.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListMessagesResponseDto {
    private String message;
    private List<Message> messages;
}
