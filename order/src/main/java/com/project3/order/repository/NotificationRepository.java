package com.project3.order.repository;

import com.project3.order.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    @Query("{ 'userTo': ?0 }")
    List<Notification> findNotificationByUserToId(String userToId);

}
