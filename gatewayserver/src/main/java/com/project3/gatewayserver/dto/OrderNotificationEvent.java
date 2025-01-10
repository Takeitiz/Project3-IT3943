package com.project3.gatewayserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderNotificationEvent {
    private Order order;
    private OrderNotification notification;
}
