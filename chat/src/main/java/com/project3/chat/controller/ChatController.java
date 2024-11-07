package com.project3.chat.controller;

import com.project3.chat.dto.MessageDto;
import com.project3.chat.entity.Conversation;
import com.project3.chat.entity.Message;
import com.project3.chat.service.ChatService;
import com.project3.chat.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;

@RestController
@RequestMapping(path="/api/message", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class ChatController {

    private final ConversationService conversationService;

    @PostMapping("/")
    public ResponseEntity<Message> createMessage(@Valid @RequestBody MessageDto messageDto) {

       /*
            Todo: Handle file upload
        */

        Message message = new Message();
        message.setConversationId(messageDto.getConversationId());
        message.setBody(messageDto.getBody());
        message.setGigId(messageDto.getGigId());
        message.setBuyerId(messageDto.getBuyerId());
        message.setSellerId(messageDto.getSellerId());
        message.setSenderUsername(messageDto.getSenderUsername());
        message.setReceiverUsername(messageDto.getReceiverUsername());
        message.setSenderPicture(messageDto.getSenderPicture());
        message.setReceiverPicture(messageDto.getReceiverPicture());
        message.setRead(messageDto.getIsRead());
        message.setHasOffer(messageDto.getHasOffer());
        message.setOffer(messageDto.getOffer());

        // Todo: Add set file's stuffs

        if (!messageDto.getHasConversationId()) {
            Conversation conversation = new Conversation(messageDto.getConversationId(), messageDto.getSenderUsername(), messageDto.getReceiverUsername());
            conversationService.createConversation(conversation);
        }

        Message savedMessage = conversationService.addMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(message);
    }

    @PutMapping("/offer")
    public ResponseEntity<Message> updateCustomOffer(@RequestParam String messageId, @RequestParam String type) {
        Message message = conversationService.updateOffer(messageId, type);
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }

    @PutMapping("/mark-as-read")
    public ResponseEntity<Message> markMessagesAsRead(@RequestParam String messageId) {
        Message message = conversationService.markMessageAsRead(messageId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }


    @PutMapping("/mark-multiple-as-read")
    public ResponseEntity<Message> markMultipleMessagesAsRead(@RequestParam String messageId, @RequestParam String senderUsername, @RequestParam String receiverUsername) {
        Message message = conversationService.markManyMessagesAsRead(receiverUsername, senderUsername, messageId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(message);
    }

    @GetMapping("/conversation/{senderUsername}/{receiverUsername}")
    public ResponseEntity<Conversation> getConversation(@PathVariable String senderUsername, @PathVariable String receiverUsername) {
        Conversation conversation = conversationService.getConversation(senderUsername, receiverUsername);
        return ResponseEntity.status(HttpStatus.OK)
                .body(conversation);
    }

    @GetMapping("/conversation/{username}")
    public ResponseEntity<List<Message>> getConversationListsByUsername(@PathVariable String username) {
        List<Message> messages = conversationService.getUserConversationList(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(messages);
    }

    @GetMapping("/{senderUsername}/{receiverUsername}")
    public ResponseEntity<List<Message>> getMessagesBySenderNameAndReceiverName(@PathVariable String senderUsername, @PathVariable String receiverUsername) {
        List<Message> messages = conversationService.getMessages(senderUsername, receiverUsername);
        return ResponseEntity.status(HttpStatus.OK)
                .body(messages);
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Message>> getMessagesByConversationId(@PathVariable String conversationId) {
        List<Message> messages = conversationService.getUserMessages(conversationId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(messages);
    }
}
