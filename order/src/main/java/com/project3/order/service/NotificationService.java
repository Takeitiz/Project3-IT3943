package com.project3.order.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.project3.order.entity.Notification;
import com.project3.order.repository.NotificationRepository;
import com.project3.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SocketIOServer socketIOServer;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserToId(String userToId) {
        return notificationRepository.findNotificationByUserToId(userToId);
    }


}
