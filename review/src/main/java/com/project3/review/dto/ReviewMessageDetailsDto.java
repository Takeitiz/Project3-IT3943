package com.project3.review.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewMessageDetailsDto {
    private String gigId;
    private String reviewerId;
    private String sellerId;
    private String review;
    private Integer rating;
    private String orderId;
    private String type;
    private String createdAt;
}
