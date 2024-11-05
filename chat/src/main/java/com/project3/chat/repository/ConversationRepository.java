package com.project3.chat.repository;

import com.project3.chat.entity.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    @Query("{ $or: [ " +
            "{ 'senderUsername': ?0, 'receiverUsername': ?1 }, " +
            "{ 'senderUsername': ?1, 'receiverUsername': ?0 } " +
            "] }")
    Conversation findConversationBetween(String senderUsername, String receiverUsername);
}
