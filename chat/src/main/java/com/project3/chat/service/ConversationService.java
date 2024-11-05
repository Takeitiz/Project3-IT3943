package com.project3.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.project3.chat.dto.MessageDetailsDto;
import com.project3.chat.dto.MessageDto;
import com.project3.chat.entity.Conversation;
import com.project3.chat.entity.Message;
import com.project3.chat.exception.ResourceNotFoundException;
import com.project3.chat.kafka.ChatProducer;
import com.project3.chat.repository.ConversationRepository;
import com.project3.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ChatProducer chatProducer;
    private final SocketIOServer socketIOServer;
    private final MongoTemplate mongoTemplate;


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

    public Conversation getConversation(String senderUsername, String receiverUsername) {
        return conversationRepository.findConversationBetween(senderUsername, receiverUsername);
    }

    public List<Message> getUserConversationList(String username) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("senderUsername").is(username),
                Criteria.where("receiverUsername").is(username)
        );

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.sort(Sort.Direction.DESC, "createdAt"),
                Aggregation.group("conversationId")
                        .first(Aggregation.ROOT).as("result"),
                Aggregation.project()
                        .and("result._id").as("_id")
                        .and("result.conversationId").as("conversationId")
                        .and("result.sellerId").as("sellerId")
                        .and("result.buyerId").as("buyerId")
                        .and("result.receiverUsername").as("receiverUsername")
                        .and("result.receiverPicture").as("receiverPicture")
                        .and("result.senderUsername").as("senderUsername")
                        .and("result.senderPicture").as("senderPicture")
                        .and("result.body").as("body")
                        .and("result.file").as("file")
                        .and("result.gigId").as("gigId")
                        .and("result.isRead").as("isRead")
                        .and("result.hasOffer").as("hasOffer")
                        .and("result.createdAt").as("createdAt")
        );

        AggregationResults<Message> results = mongoTemplate.aggregate(
                aggregation, "messages", Message.class
        );

        return results.getMappedResults();
    }

    public List<Message> getMessages(String senderUsername, String receiverUsername) {
        return messageRepository.findMessagesBetweenUsers(senderUsername, receiverUsername);
    }

    public List<Message> getUserMessages(String messageConversationId) {
        return messageRepository.findUserMessages(messageConversationId);
    }

    public Message updateOffer(String messageId, String type) {
        return messageRepository.updateOffer(messageId, type);
    }

    public Message markMessageAsRead(String messageId) {
        Message message =  messageRepository.markMessageAsRead(messageId);
        socketIOServer.getBroadcastOperations().sendEvent("message updated", message);
        return message;
    }

    public Message markManyMessagesAsRead(String receiver, String sender, String messageId) {
        messageRepository.markManyMessagesAsRead(sender, receiver);
        Message message = messageRepository.findMessageById(messageId).orElseThrow(
                () -> new ResourceNotFoundException("Message", "MessageId", messageId)
        );
        socketIOServer.getBroadcastOperations().sendEvent("message updated", message);
        return message;
    }
}
