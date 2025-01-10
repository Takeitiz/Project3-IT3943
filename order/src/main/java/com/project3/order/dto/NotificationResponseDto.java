package com.project3.order.dto;

import com.project3.order.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDto {
    private String message;
    private Notification notification;
}
