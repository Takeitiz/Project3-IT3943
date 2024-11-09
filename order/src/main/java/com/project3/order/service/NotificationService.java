package com.project3.order.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.project3.order.dto.OrderDto;
import com.project3.order.entity.Notification;
import com.project3.order.entity.Order;
import com.project3.order.repository.NotificationRepository;
import com.project3.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
    private final OrderRepository orderRepository;
    private final SocketIOServer socketIOServer;
    private final MongoTemplate mongoTemplate;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserToId(String userToId) {
        return notificationRepository.findNotificationByUserToId(userToId);
    }

    @Transactional
    public Notification markNotificationAsRead(String notificationId) {
        Query query = new Query(Criteria.where("_id").is(notificationId));
        Update update = new Update().set("isRead", true);

        Notification updatedNotification = mongoTemplate.findAndModify(query, update, Notification.class);

        if (updatedNotification == null) {
            throw new RuntimeException("Notification not found with id: " + notificationId);
        }

        Order order = orderRepository.findByOrderId(updatedNotification.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + updatedNotification.getOrderId()));

        socketIOServer.getBroadcastOperations().sendEvent("order notification", order, updatedNotification);

        return updatedNotification;
    }

    // Tạm thời chưa dùng OrderDto vì chưa viết Mapping giữa OrderDto và Order

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
