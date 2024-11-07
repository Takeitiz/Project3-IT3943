package com.project3.chat.dto;

import com.project3.chat.entity.Message;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageDto {
    private String conversationId;

    private String id;

    private String body;

    private Boolean hasConversationId;

    private String file;

    private String fileType;

    private String fileName;

    private String fileSize;

    private String gigId;

    @NotBlank(message = "Seller id is required")
    private String sellerId;

    @NotBlank(message = "Buyer id is required")
    private String buyerId;

    @NotBlank(message = "Sender username is required")
    private String senderUsername;

    @NotBlank(message = "Sender picture is required")
    private String senderPicture;

    @NotBlank(message = "Receiver username is required")
    private String receiverUsername;

    @NotBlank(message = "Receiver picture is required")
    private String receiverPicture;

    private Boolean isRead;

    private Boolean hasOffer;

    @Valid
    private Message.Offer offer;

    private String createdAt;

}


