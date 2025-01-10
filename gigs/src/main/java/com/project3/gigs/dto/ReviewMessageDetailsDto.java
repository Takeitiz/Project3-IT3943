package com.project3.gigs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
