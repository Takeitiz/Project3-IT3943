package com.project3.chat.controller;

import com.project3.chat.dto.*;
import com.project3.chat.entity.Conversation;
import com.project3.chat.entity.Message;
import com.project3.chat.service.CloudinaryService;
import com.project3.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/api/v1/message", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class ChatController {

    private final ConversationService conversationService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/")
    public ResponseEntity<?> createMessage(@RequestBody MessageDto messageDto) {

        String file = messageDto.getFile();
        String randomCharacters = generateRandomHexToken(20);
        String fileType = messageDto.getFileType();

        if (StringUtils.hasText(file)) {
            String fileName = fileType.equals("zip")
                    ? randomCharacters + ".zip"
                    : randomCharacters;
            Map uploadResult = cloudinaryService.upload(file, fileName, null, null);
            if (uploadResult.get("public_id") == null) {
                ResponseEntity.badRequest().body("File upload error. Try again.', 'Create message() method");
            }
            file = uploadResult.get("secure_url").toString();
        }

        Message message = new Message();
        message.setConversationId(messageDto.getConversationId());
        message.setBody(messageDto.getBody());
        message.setFile(file);
        message.setFileType(fileType);
        message.setFileSize(messageDto.getFileSize());
        message.setFileName(messageDto.getFileName());
        message.setGigId(messageDto.getGigId());
        message.setBuyerId(messageDto.getBuyerId());
        message.setSellerId(messageDto.getSellerId());
        message.setSenderUsername(messageDto.getSenderUsername());
        message.setReceiverUsername(messageDto.getReceiverUsername());
        message.setSenderPicture(messageDto.getSenderPicture());
        message.setReceiverPicture(messageDto.getReceiverPicture());
        message.setRead(messageDto.isRead());
        message.setHasOffer(messageDto.isHasOffer());
        message.setOffer(messageDto.getOffer());

        if (!messageDto.getHasConversationId()) {
            conversationService.createConversation(messageDto.getConversationId(), messageDto.getSenderUsername(), messageDto.getReceiverUsername());
        }

        Message savedMessage = conversationService.addMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MessageResponseDto("Message added", messageDto.getConversationId(), savedMessage));
    }

    @PutMapping("/offer")
    public ResponseEntity<?> updateCustomOffer(@RequestBody OfferMessageDto offerMessageDto) {
        Message message = conversationService.updateOffer(offerMessageDto.getMessageId(), offerMessageDto.getType());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SingleMessageResponseDto("Message updated", message));
    }

    @PutMapping("/mark-as-read")
    public ResponseEntity<?> markMessagesAsRead(@RequestBody MarkSingleMessageDto markSingleMessageDto) {
        Message message = conversationService.markMessageAsRead(markSingleMessageDto.getMessageId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SingleMessageResponseDto("Message marked as read", message));
    }


    @PutMapping("/mark-multiple-as-read")
    public ResponseEntity<?> markMultipleMessagesAsRead(@RequestBody MarkMultipleMessageDto markMultipleMessageDto) {
        Message message = conversationService.markManyMessagesAsRead(markMultipleMessageDto.getReceiverUsername(), markMultipleMessageDto.getSenderUsername(), markMultipleMessageDto.getMessageId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SingleMessageResponseDto("Messages marked as read", message));
    }

    @GetMapping("/conversation/{senderUsername}/{receiverUsername}")
    public ResponseEntity<?> getConversation(@PathVariable String senderUsername, @PathVariable String receiverUsername) {
        List<Conversation> conversations = conversationService.getConversation(senderUsername, receiverUsername);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ConversationResponseDto("Chat conversation", conversations));
    }

    @GetMapping("/{senderUsername}/{receiverUsername}")
    public ResponseEntity<?> getMessagesBySenderNameAndReceiverName(@PathVariable String senderUsername, @PathVariable String receiverUsername) {
        List<Message> messages = conversationService.getMessages(senderUsername, receiverUsername);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ListMessagesResponseDto("Chat messages", messages));
    }

    @GetMapping("/conversations/{username}")
    public ResponseEntity<?> getConversationListsByUsername(@PathVariable String username) {
        List<Message> messages = conversationService.getUserConversationList(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ConversationListDto("Conversation list", messages));
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<?> getMessagesByConversationId(@PathVariable String conversationId) {
        List<Message> messages = conversationService.getUserMessages(conversationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ListMessagesResponseDto("Chat messages", messages));
    }

    private String generateRandomHexToken(int bytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[bytes];
        secureRandom.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }
}
