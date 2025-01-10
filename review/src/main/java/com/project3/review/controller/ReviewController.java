package com.project3.review.controller;

import com.project3.review.entity.Review;
import com.project3.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/review", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.addReview(review);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdReview);
    }

    @GetMapping("/gig/{gigId}")
    public ResponseEntity<List<Review>>  getReviewsByGigId(@PathVariable String gigId) {
        List<Review> reviews = reviewService.getReviewsByGigId(gigId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviews);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Review>> getReviewsBySellerId(@PathVariable String sellerId) {
        List<Review> reviews = reviewService.getReviewsBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(reviews);
    }
}
