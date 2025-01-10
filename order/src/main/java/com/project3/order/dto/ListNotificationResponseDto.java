package com.project3.order.dto;

import com.project3.order.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListNotificationResponseDto {
    private String message;
    private List<Notification> notifications;
}
