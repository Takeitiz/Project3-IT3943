package com.project3.order.dto;

import lombok.Data;

@Data
public class CancelOrderRequestDto {
    private OrderMessageDto order;
    private String paymentIntent;
}
