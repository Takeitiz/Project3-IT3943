package com.project3.order.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.project3.order.entity.Notification;
import com.project3.order.entity.Order;
import com.project3.order.repository.NotificationRepository;
import com.project3.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SocketIOServer socketIOServer;
    private final MongoTemplate mongoTemplate;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserToId(String userToId) {
        Query query = new Query(Criteria.where("userTo").is(userToId));
        return mongoTemplate.find(query, Notification.class);
    }

    @Transactional
    public Notification markNotificationAsRead(String notificationId) {
        Query query = new Query(Criteria.where("_id").is(notificationId));
        Update update = new Update().set("isRead", true);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Notification updatedNotification = mongoTemplate.findAndModify(query, update,options, Notification.class);

        if (updatedNotification == null) {
            throw new RuntimeException("Notification not found with id: " + notificationId);
        }

        query = new Query(Criteria.where("orderId").is(updatedNotification.getOrderId()));
        Order order = mongoTemplate.findOne(query, Order.class);

        socketIOServer.getBroadcastOperations().sendEvent("order notification", order, updatedNotification);

        return updatedNotification;
    }

    public void sendNotification(Order order, String userToId, String message) {
        Notification notification = new Notification();
        notification.setUserTo(userToId);
        notification.setSenderUsername(order.getSellerUsername());
        notification.setSenderPicture(order.getSellerImage());
        notification.setReceiverUsername(order.getBuyerUsername());
        notification.setReceiverPicture(order.getBuyerImage());
        notification.setMessage(message);
        notification.setOrderId(order.getOrderId());

        Notification savedNotification = notificationRepository.save(notification);
        socketIOServer.getBroadcastOperations().sendEvent("order notification", order, savedNotification);
    }


}
