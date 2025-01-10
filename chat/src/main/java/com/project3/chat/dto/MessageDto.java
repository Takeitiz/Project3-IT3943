package com.project3.chat.dto;

import com.project3.chat.entity.Message;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
    private String id;
    private String conversationId;
    private String senderUsername;
    private String receiverUsername;
    private String senderPicture;
    private String receiverPicture;
    private Boolean hasConversationId;
    private String body;
    private String file;
    private String fileType;
    private String fileSize;
    private String fileName;
    private String gigId;
    private String buyerId;
    private String sellerId;
    private boolean isRead;
    private boolean hasOffer;
    private Message.Offer offer;
    private String createdAt;  // Changed to String for better JSON serialization

    // Converter method from Message to MessageDTO
    public static MessageDto fromMessage(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderUsername(message.getSenderUsername())
                .receiverUsername(message.getReceiverUsername())
                .senderPicture(message.getSenderPicture())
                .receiverPicture(message.getReceiverPicture())
                .body(message.getBody())
                .file(message.getFile())
                .fileType(message.getFileType())
                .fileSize(message.getFileSize())
                .fileName(message.getFileName())
                .gigId(message.getGigId())
                .buyerId(message.getBuyerId())
                .sellerId(message.getSellerId())
                .isRead(message.isRead())
                .hasOffer(message.isHasOffer())
                .offer(message.getOffer())
                .createdAt(message.getCreatedAt() != null ?
                        message.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }

}