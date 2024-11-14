package com.project3.review.service;

import com.project3.review.dto.ReviewMessageDetailsDto;
import com.project3.review.entity.Review;
import com.project3.review.kafka.ReviewProducer;
import com.project3.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewProducer reviewProducer;
    private final MongoTemplate mongoTemplate;

    public Review addReview(Review review) {

        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        ReviewMessageDetailsDto messageDetails = new ReviewMessageDetailsDto();
        messageDetails.setGigId(savedReview.getGigId());
        messageDetails.setReviewerId(savedReview.getReviewerId());
        messageDetails.setSellerId(savedReview.getSellerId());
        messageDetails.setReview(savedReview.getReview());
        messageDetails.setRating(savedReview.getRating());
        messageDetails.setOrderId(savedReview.getOrderId());
        messageDetails.setCreatedAt(savedReview.getCreatedAt());
        messageDetails.setType(savedReview.getReviewType());

        reviewProducer.sendJobberReviewTopic(messageDetails);

        return savedReview;
    }

    public List<Review> getReviewsByGigId(String gigId) {
        Query query = new Query(Criteria.where("gigId").is(gigId));
        return mongoTemplate.find(query, Review.class);
    }

    public List<Review> getReviewsBySellerId(String sellerId) {
        Query query = new Query(Criteria.where("sellerId").is(sellerId)
                .and("reviewType").is("seller-review"));
        return mongoTemplate.find(query, Review.class);
    }
}
