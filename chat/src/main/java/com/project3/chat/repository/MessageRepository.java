package com.project3.chat.repository;

import com.project3.chat.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    @Query(value = "{ $or: [ " +
            "{ 'senderUsername': ?0, 'receiverUsername': ?1 }, " +
            "{ 'senderUsername': ?1, 'receiverUsername': ?0 } " +
            "] }",
            sort = "{ 'createdAt': 1 }")
    List<Message> findMessagesBetweenUsers(String sender, String receiver);

    @Query(value = "{ 'conversationId': ?0 }", sort = "{ 'createdAt': 1 }")
    List<Message> findUserMessages(String conversationId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'offer.?1': true } }")
    Message updateOffer(String messageId, String type);

    @Query("{'_id': ?0 }")
    @Update("{ '$set':  { 'isRead': true }}")
    Message markMessageAsRead(String messageId);

    @Query("{'senderUsername': ?0, 'receiverUsername': ?1, 'isRead': false}")
    @Update("{ '$set': { 'isRead': true }}")
    void markManyMessagesAsRead(String sender, String receiver);

    Optional<Message> findMessageById(String messageId);
}
