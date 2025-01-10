package com.project3.order.controller;

import com.project3.order.dto.*;
import com.project3.order.entity.Notification;
import com.project3.order.entity.Order;
import com.project3.order.repository.OrderRepository;
import com.project3.order.service.CloudinaryService;
import com.project3.order.service.NotificationService;
import com.project3.order.service.OrderService;
import com.project3.order.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/api/v1/order", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class OrderController {

    private final StripeService stripeService;
    private final OrderService orderService;
    private final NotificationService notificationService;
    private final CloudinaryService cloudinaryService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequestDto paymentRequest) {
        try {
            String customerId = stripeService.createOrRetrieveCustomer(paymentRequest.getEmail(), paymentRequest.getBuyerId());
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(paymentRequest.getPrice(), customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new PaymentResponseDto("Order intent created successfully",
                    paymentIntent.getClientSecret(), paymentIntent.getId()));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Payment failed: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        double serviceFee = order.getPrice() < 50
                ? BigDecimal.valueOf((5.5 / 100) * order.getPrice() + 2).setScale(2, RoundingMode.HALF_UP).doubleValue()
                : BigDecimal.valueOf((5.5 / 100) * order.getPrice()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        order.setServiceFee(serviceFee);
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponseDto("Order created successfully.", savedOrder));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto("Order created successfully.", order));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getOrdersBySellerId(@PathVariable String sellerId) {
        List<Order> orders = orderService.getOrdersBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ListOrderResponseDto("Seller orders", orders));
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> getOrdersByBuyerId(@PathVariable String buyerId) {
        List<Order> orders = orderService.getOrdersByBuyerId(buyerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ListOrderResponseDto("Buyer orders", orders));
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId, @RequestBody CancelOrderRequestDto cancelOrderRequestDto) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(cancelOrderRequestDto.getPaymentIntent())
                    .build();
            Refund.create(params);
            orderService.cancelOrder(orderId, cancelOrderRequestDto.getOrder());
            return ResponseEntity.status(HttpStatus.OK).body("Order cancelled successfully.");
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process refund: " + e.getMessage());
        }
    }

    @PutMapping("/extension/{orderId}")
    public ResponseEntity<?> requestExtension(@PathVariable String orderId, @RequestBody ExtendedDeliveryDto extendedDeliveryDto) {
        Order order = orderService.requestDeliveryExtension(orderId, extendedDeliveryDto);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto("Order delivery request", order));
    }

    @PutMapping("/gig/{type}/{orderId}")
    public ResponseEntity<?> extensionApproval(@PathVariable String type, @PathVariable String orderId, @RequestBody ExtendedDeliveryDto extendedDeliveryDto) {
        Order order;
        if ("approve".equalsIgnoreCase(type)) {
            order = orderService.approveDeliveryDate(orderId, extendedDeliveryDto);
        } else {
            order = orderService.rejectDeliveryDate(orderId, extendedDeliveryDto.getDeclineReason());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto("Order delivery date extension", order));
    }

    @PutMapping("/approve-order/{orderId}")
    public ResponseEntity<?> approveOrder(@PathVariable String orderId, @RequestBody OrderMessageDto orderMessageDto) {
        Order order = orderService.approveOrder(orderId, orderMessageDto);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto("Order approved successfully.", order));
    }

    @PutMapping("/deliver-order/{orderId}")
    public ResponseEntity<?> deliverOrder(@PathVariable String orderId, @RequestBody DeliveredWorkDto deliveredWorkDto) {
        String file = deliveredWorkDto.getFile();
        String randomCharacters = generateRandomHexToken(20);
        String fileType = deliveredWorkDto.getFileType();

        if (StringUtils.hasText(file)) {
            String fileName = fileType.equals("zip")
                    ? randomCharacters + ".zip"
                    : randomCharacters;
            Map uploadResult = cloudinaryService.upload(file, fileName, null, null);
            if (uploadResult.get("public_id") == null) {
                ResponseEntity.badRequest().body("File upload error. Try again.', 'Create message() method");
            }
            file = uploadResult.get("secure_url").toString();
        }
        deliveredWorkDto.setFile(file);
        Order order = orderService.sellerDeliverOrder(orderId,true, deliveredWorkDto);
        return ResponseEntity.status(HttpStatus.OK).body(new OrderResponseDto("Order delivered successfully.", order));
    }

    @GetMapping("/notification/{userTo}")
    public ResponseEntity<?> getNotifications(@PathVariable String userTo) {
        List<Notification> notifications = notificationService.getNotificationsByUserToId(userTo);
        return ResponseEntity.status(HttpStatus.OK).body(new ListNotificationResponseDto("Notifications", notifications));
    }

    @PutMapping("/notification/mark-as-read")
    public ResponseEntity<?> markAsRead(@RequestBody MarkAsReadDto markAsReadDto) {
        Notification notification = notificationService.markNotificationAsRead(markAsReadDto.getNotificationId());
        return ResponseEntity.status(HttpStatus.OK).body(new NotificationResponseDto("Notification updated successfully.", notification));
    }

    private String generateRandomHexToken(int bytes) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[bytes];
        secureRandom.nextBytes(token);
        return HexFormat.of().formatHex(token);
    }
    
}
