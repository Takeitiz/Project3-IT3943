package com.project3.chat.dto;

import com.project3.chat.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleMessageResponseDto {
    private String message;
    private Message singleMessage;

}
