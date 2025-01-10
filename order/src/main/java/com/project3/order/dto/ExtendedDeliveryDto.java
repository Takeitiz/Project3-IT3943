package com.project3.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExtendedDeliveryDto {
    private String originalDate;
    private String newDate;
    private Integer days;
    private String reason;
    private String deliveryDateUpdate;
    private String declineReason;
}
