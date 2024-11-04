package com.project3.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.project3.chat.dto.MessageDetailsDto;
import com.project3.chat.dto.MessageDto;
import com.project3.chat.entity.Conversation;
import com.project3.chat.entity.Message;
import com.project3.chat.kafka.ChatProducer;
import com.project3.chat.repository.ConversationRepository;
import com.project3.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ChatProducer chatProducer;
    private final SocketIOServer socketIOServer;

    public void createConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    public Message addMessage(Message message) {
        message.setCreatedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        if (message.isHasOffer()) {
            MessageDetailsDto messageDetailsDto = new MessageDetailsDto(
                    message.getSenderUsername(),
                    message.getOffer().getPrice().toString(),
                    message.getReceiverUsername().toLowerCase(),
                    message.getSenderUsername().toLowerCase(),
                    message.getOffer().getGigTitle(),
                    message.getOffer().getDescription(),
                    message.getOffer().getDeliveryInDays().toString(),
                    "offer"
            );
            chatProducer.publicDirectMessage(messageDetailsDto);
        }

        socketIOServer.getBroadcastOperations().sendEvent("message received", savedMessage);
        return savedMessage;
    }
}
