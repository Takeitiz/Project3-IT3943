package com.project3.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExtendedDeliveryDto {
    LocalDateTime originalDate;
    LocalDateTime newDate;
    Integer days;
    String reason;
    LocalDateTime deliveryDateUpdate;
}
