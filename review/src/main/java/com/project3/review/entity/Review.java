package com.project3.review.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "review")
public class Review {
    @Id
    private String id;

    @Indexed
    private String gigId;

    private String reviewerId;
    private String orderId;

    @Indexed
    private String sellerId;

    private String review;
    private String reviewerImage;
    private String reviewerUsername;
    private String country;
    private String reviewType;
    private int rating;
    private LocalDateTime createdAt;
}
