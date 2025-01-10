package com.project3.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.mongodb.client.result.UpdateResult;
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
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ChatProducer chatProducer;
    private final SocketIOServer socketIOServer;
    private final MongoTemplate mongoTemplate;

    public void createConversation(String conversationId, String sender, String receiver) {
        Conversation conversation = new Conversation(conversationId, sender, receiver);
        conversationRepository.save(conversation);
    }

    public Message addMessage(Message message) {
        message.setCreatedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        MessageDto messageDTO = MessageDto.fromMessage(savedMessage);

        socketIOServer.getBroadcastOperations().sendEvent("message received", messageDTO);
        return savedMessage;
    }

    public List<Conversation> getConversation(String sender, String receiver) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("senderUsername").is(sender)
                        .and("receiverUsername").is(receiver),
                Criteria.where("senderUsername").is(receiver)
                        .and("receiverUsername").is(sender)
        );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Conversation.class);
    }

    public List<Message> getUserConversationList(String username) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("senderUsername").is(username),
                Criteria.where("receiverUsername").is(username)
        );
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Message> allMessages = mongoTemplate.find(query, Message.class);
        Map<String, Message> latestMessagesByConversation = new LinkedHashMap<>();
        allMessages.forEach(message -> {
            String conversationId = message.getConversationId();
            latestMessagesByConversation.putIfAbsent(conversationId, message);
        });
        return new ArrayList<>(latestMessagesByConversation.values());
    }

    public List<Message> getMessages(String sender, String receiver) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("senderUsername").is(sender)
                        .and("receiverUsername").is(receiver),
                Criteria.where("senderUsername").is(receiver)
                        .and("receiverUsername").is(sender)
        );
        Query query = new Query(criteria);
        query.with(Sort.by(Sort.Direction.ASC, "createdAt"));
        return mongoTemplate.find(query, Message.class);
    }

    public List<Message> getUserMessages(String messageConversationId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("conversationId").is(messageConversationId));
        query.with(Sort.by(Sort.Direction.ASC, "createdAt"));

        return mongoTemplate.find(query, Message.class);
    }

    public Message updateOffer(String messageId, String type) {
        Query query = new Query(Criteria.where("_id").is(messageId));
        Update update = new Update().set("offer." + type, true);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        return mongoTemplate.findAndModify(
                query,
                update,
                options,
                Message.class
        );
    }

    public Message markMessageAsRead(String messageId) {
        Query query = new Query(Criteria.where("_id").is(messageId));
        Update update = new Update().set("isRead", true);
        FindAndModifyOptions options = FindAndModifyOptions.options().returnNew(true);
        Message updatedMessage = mongoTemplate.findAndModify(
                query,
                update,
                options,
                Message.class
        );
        socketIOServer.getBroadcastOperations().sendEvent("message updated", updatedMessage);
        return updatedMessage;
    }

    public Message markManyMessagesAsRead(String receiver, String sender, String messageId) {
        Query updateQuery = new Query(Criteria.where("senderUsername").is(sender)
                .and("receiverUsername").is(receiver)
                .and("isRead").is(false));
        Update update = new Update()
                .set("isRead", true);
        UpdateResult updateResult = mongoTemplate.updateMulti(
                updateQuery,
                update,
                Message.class
        );
        Query findQuery = new Query(Criteria.where("_id").is(messageId));
        Message message = mongoTemplate.findOne(findQuery, Message.class);
        socketIOServer.getBroadcastOperations().sendEvent("message updated", message);
        return message;
    }
}
