package com.project3.order.controller;

import com.project3.order.dto.*;
import com.project3.order.entity.Notification;
import com.project3.order.entity.Order;
import com.project3.order.repository.OrderRepository;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping(path="/api/order", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class OrderController {

    private final StripeService stripeService;
    private final OrderService orderService;
    private final NotificationService notificationService;

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

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        double serviceFee = order.getPrice() < 50
                ? BigDecimal.valueOf((5.5 / 100) * order.getPrice() + 2).setScale(2, RoundingMode.HALF_UP).doubleValue()
                : BigDecimal.valueOf((5.5 / 100) * order.getPrice()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        order.setServiceFee(serviceFee);
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderService.getOrderByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Order>> getOrdersBySellerId(@PathVariable String sellerId) {
        List<Order> orders = orderService.getOrdersBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<Order>> getOrdersByBuyerId(@PathVariable String buyerId) {
        List<Order> orders = orderService.getOrdersByBuyerId(buyerId);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String orderId, @RequestBody CancelOrderRequestDto cancelOrderRequestDto) {
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
    public ResponseEntity<Order> requestExtension(@PathVariable String orderId, @RequestBody ExtendedDeliveryDto extendedDeliveryDto) {
        Order order = orderService.requestDeliveryExtension(orderId, extendedDeliveryDto);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("/gig/{type}/{orderId}")
    public ResponseEntity<Order> extensionApproval(@PathVariable String type, @PathVariable String orderId, @RequestBody ExtendedDeliveryDto extendedDeliveryDto) {
        Order order;
        if ("approve".equalsIgnoreCase(type)) {
            order = orderService.approveDeliveryDate(orderId, extendedDeliveryDto);
        } else {
            order = orderService.rejectDeliveryDate(orderId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("/approve-order/{orderId}")
    public ResponseEntity<Order> approveOrder(@PathVariable String orderId, @RequestBody OrderMessageDto orderMessageDto) {
        Order order = orderService.approveOrder(orderId, orderMessageDto);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @PutMapping("/deliver-order/{orderId}")
    public ResponseEntity<Order> deliverOrder(@PathVariable String orderId, @RequestBody DeliveredWorkDto deliveredWorkDto) {
        Order order = orderService.sellerDeliverOrder(orderId,true, deliveredWorkDto);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @GetMapping("/notification/{userTo}")
    public ResponseEntity<List<Notification>> getNotifications(@PathVariable String userTo) {
        List<Notification> notifications = notificationService.getNotificationsByUserToId(userTo);
        return ResponseEntity.status(HttpStatus.OK).body(notifications);
    }

    @PutMapping("/notification/mark-as-read")
    public ResponseEntity<Notification> markAsRead(@RequestParam String notificationId) {
        Notification notification = notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.status(HttpStatus.OK).body(notification);
    }
    
}
