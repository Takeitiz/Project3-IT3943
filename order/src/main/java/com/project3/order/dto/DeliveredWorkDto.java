package com.project3.order.dto;

import lombok.Data;

@Data
public class DeliveredWorkDto {
    String message;
    String file;
    String fileType;
    String fileSize;
    String fileName;
    String orderDelivered;
}
