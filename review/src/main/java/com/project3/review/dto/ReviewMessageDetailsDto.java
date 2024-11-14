package com.project3.review.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewMessageDetailsDto {
    String gigId;
    String reviewerId;
    String sellerId;
    String review;
    Integer rating;
    String orderId;
    String type;
    LocalDateTime createdAt;
}
