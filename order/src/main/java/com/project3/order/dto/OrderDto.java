package com.project3.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDto {

    @NotNull
    @Valid
    private OfferDto offer;

    @NotNull
    private String gigId;

    @NotNull
    private String sellerId;

    @NotNull
    private String sellerUsername;

    @NotNull
    @Email
    private String sellerEmail;

    @NotNull
    private String sellerImage;

    @NotNull
    private String gigCoverImage;

    @NotNull
    private String gigMainTitle;

    @NotNull
    private String gigBasicTitle;

    @NotNull
    private String gigBasicDescription;

    @NotNull
    private String buyerId;

    @NotNull
    private String buyerUsername;

    @NotNull
    @Email
    private String buyerEmail;

    @NotNull
    private String buyerImage;

    @NotNull
    private String status;

    @NotNull
    private String orderId;

    @NotNull
    private String invoiceId;

    @NotNull
    private Integer quantity;

    @NotNull
    private Double price;

    private Double serviceFee;

    private String requirements;

    @NotNull
    private String paymentIntent;

    @Valid
    private RequestExtensionDto requestExtension;

    private Boolean delivered;

    private LocalDateTime approvedAt;

    private List<@Valid DeliveredWorkDto> deliveredWork;

    private LocalDateTime dateOrdered;

    @Valid
    private EventsDto events;

    @Valid
    private ReviewDto buyerReview;

    @Valid
    private ReviewDto sellerReview;

    @Data
    public static class OfferDto {
        @NotNull
        private String gigTitle;

        @NotNull
        private Double price;

        @NotNull
        private String description;

        @NotNull
        private Integer deliveryInDays;

        @NotNull
        private String oldDeliveryDate;

        private String newDeliveryDate;

        @NotNull
        private Boolean accepted;

        @NotNull
        private Boolean cancelled;
    }


    @Data
    public static class RequestExtensionDto {
        @NotNull
        private String originalDate;

        @NotNull
        private String newDate;

        @NotNull
        private Integer days;

        @NotNull
        private String reason;
    }

    @Data
    public static class DeliveredWorkDto {
        private String message;
        private String file;
    }

    @Data
    public static class EventsDto {
        private String placeOrder;
        private String requirements;
        private String orderStarted;
        private String deliverydateUpdate;
        private String orderDelivered;
        private String buyerReview;
        private String sellerReview;
    }

    @Data
    public static class ReviewDto {
        private Integer rating;
        private String review;
    }
}
