package com.project3.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponseDto {
    private String message;
    private String clientSecret;
    private String paymentIntentId;
}
