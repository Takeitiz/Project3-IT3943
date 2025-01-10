package com.project3.gatewayserver.dto;

import lombok.Data;
import org.springframework.stereotype.Indexed;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {

    private Offer offer;

    private String gigId;

    private String sellerId;
    private String sellerUsername;
    private String sellerImage;
    private String sellerEmail;
    private String gigCoverImage;
    private String gigMainTitle;
    private String gigBasicTitle;
    private String gigBasicDescription;
    private Integer countExtension;
    private String buyerId;
    private String buyerUsername;
    private String buyerEmail;
    private String buyerImage;
    private String status;

    private String invoiceId;
    private String orderId;
    private Integer quantity;
    private Double price;
    private Double serviceFee = 0.0;
    private String requirements = "";
    private Boolean approved = false;
    private Boolean delivered = false;
    private Boolean cancelled = false;
    private String approvedAt;
    private String paymentIntent;
    private List<DeliveredWork> deliveredWork;
    private RequestExtension requestExtension;
    private String dateOrdered;
    private Events events;
    private Review buyerReview;
    private Review sellerReview;

    @Data
    public static class Offer {
        private String gigTitle;
        private Double price;
        private String description;
        private Integer deliveryInDays;
        private String oldDeliveryDate;
        private String newDeliveryDate;
        private Boolean accepted;
        private Boolean cancelled;
        private String reason = "";
    }

    @Data
    public static class DeliveredWork {
        private String message;
        private String file;
        private String fileType;
        private String fileSize;
        private String fileName;
    }

    @Data
    public static class RequestExtension {
        private String originalDate = "";
        private String newDate = "";
        private Integer days = 0;
        private String reason = "";
    }

    @Data
    public static class Events {
        private String placeOrder;
        private String requirements;
        private String orderStarted;
        private String deliveryDateUpdate;
        private String orderDelivered;
        private String buyerReview;
        private String sellerReview;
    }

    @Data
    public static class Review {
        private Integer rating = 0;
        private String review = "";
        private String created;
    }
}

