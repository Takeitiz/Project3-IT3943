package com.project3.order.dto;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private String email;
    private double price;
    private String buyerId;
}
