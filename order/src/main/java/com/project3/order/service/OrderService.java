package com.project3.order.service;

import com.mongodb.client.MongoClient;
import com.project3.order.dto.DeliveredWorkDto;
import com.project3.order.dto.ExtendedDeliveryDto;
import com.project3.order.dto.OrderMessageDto;
import com.project3.order.dto.ReviewMessageDetailsDto;
import com.project3.order.entity.Order;
import com.project3.order.kafka.OrderProducer;
import com.project3.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final MongoTemplate mongoTemplate;
    private final OrderProducer orderProducer;

    public Order getOrderByOrderId(String orderId) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        return mongoTemplate.findOne(query, Order.class);
    }

    public List<Order> getOrdersBySellerId(String sellerId) {
        Query query = new Query(Criteria.where("sellerId").is(sellerId));
        return mongoTemplate.find(query, Order.class);
    }

    public List<Order> getOrdersByBuyerId(String buyerId) {
        Query query = new Query(Criteria.where("buyerId").is(buyerId));
        return mongoTemplate.find(query, Order.class);
    }

    public Order createOrder(Order order) {
        order.setCountExtension(0);
        order.setBuyerReview(Order.Review.builder()
                        .review("")
                        .rating(0)
                        .build());
        order.setSellerReview(Order.Review.builder()
                .review("")
                .rating(0)
                .build());
        Order savedOrder = orderRepository.save(order);

        OrderMessageDto messageDetails = new OrderMessageDto();
        messageDetails.setSellerId(order.getSellerId());
        messageDetails.setOngoingJobs(1);
        messageDetails.setType("create-order");

        orderProducer.sendUserSellerTopic(messageDetails);

        OrderMessageDto emailMessageDetails = new OrderMessageDto();
        emailMessageDetails.setOrderId(order.getOrderId());
        emailMessageDetails.setInvoiceId(order.getInvoiceId());
        emailMessageDetails.setOrderDue(order.getOffer().getNewDeliveryDate());
        emailMessageDetails.setAmount(order.getPrice());
        emailMessageDetails.setBuyerUsername(order.getBuyerUsername().toLowerCase());
        emailMessageDetails.setSellerUsername(order.getSellerUsername().toLowerCase());
        emailMessageDetails.setTitle(order.getOffer().getGigTitle());
        emailMessageDetails.setDescription(order.getOffer().getDescription());
        emailMessageDetails.setRequirements(order.getRequirements());
        emailMessageDetails.setServiceFee(order.getServiceFee());
        emailMessageDetails.setTotal(BigDecimal.valueOf(order.getPrice() + order.getServiceFee()).setScale(2, RoundingMode.HALF_UP).doubleValue());
        emailMessageDetails.setBuyerEmail(order.getBuyerEmail().toLowerCase());
        emailMessageDetails.setSellerEmail(order.getSellerEmail().toLowerCase());
        emailMessageDetails.setOrderUrl(String.format("http://localhost:3000/orders/%s/activities", order.getOrderId()));
        emailMessageDetails.setTemplateName("order-placed.html");
        emailMessageDetails.setTemplateSubject("Order Placed");

        orderProducer.sendOrderEmailTopic(emailMessageDetails);

        notificationService.sendNotification(order, order.getSellerUsername(), "placed an order for your gig.");
        return savedOrder;
    }

    public Order cancelOrder(String orderId, OrderMessageDto order) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update()
                .set("cancelled", true)
                .set("status", "Cancelled")
                .set("approvedAt", LocalDateTime.now().toString());

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order cancelOrder = mongoTemplate.findAndModify(query, update, options, Order.class);

        OrderMessageDto updateSellerInfo = new OrderMessageDto();
        updateSellerInfo.setSellerId(order.getSellerId());
        updateSellerInfo.setType("cancel-order");

        orderProducer.sendUserSellerTopic(updateSellerInfo);

        OrderMessageDto updateBuyerInfo = new OrderMessageDto();
        updateBuyerInfo.setBuyerId(order.getBuyerId());
        updateBuyerInfo.setPurchasedGigs(order.getPurchasedGigs());
        updateBuyerInfo.setType("cancel-order");

        orderProducer.sendUserBuyerTopic(updateBuyerInfo);

        assert cancelOrder != null;
        notificationService.sendNotification(cancelOrder, cancelOrder.getSellerUsername(), "cancelled your order delivery.");
        return cancelOrder;
    }

    public Order approveOrder(String orderId, OrderMessageDto order) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update()
                .set("approved", true)
                .set("status", "Completed")
                .set("approvedAt", LocalDateTime.now().toString());

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order aproveOrder = mongoTemplate.findAndModify(query, update, options, Order.class);

        OrderMessageDto updateSellerInfo = new OrderMessageDto();
        updateSellerInfo.setSellerId(order.getSellerId());
        updateSellerInfo.setBuyerId(order.getBuyerId());
        updateSellerInfo.setOngoingJobs(order.getOngoingJobs());
        updateSellerInfo.setCompletedJobs(order.getCompletedJobs());
        updateSellerInfo.setTotalEarnings(order.getTotalEarnings());
        updateSellerInfo.setRecentDelivery(LocalDateTime.now().toString());
        updateSellerInfo.setType("approve-order");

        orderProducer.sendUserSellerTopic(updateSellerInfo);

        OrderMessageDto updateBuyerInfo = new OrderMessageDto();
        updateBuyerInfo.setBuyerId(order.getBuyerId());
        updateBuyerInfo.setPurchasedGigs(order.getPurchasedGigs());
        updateBuyerInfo.setType("purchased-gigs");

        orderProducer.sendUserBuyerTopic(updateBuyerInfo);

        assert aproveOrder != null;
        notificationService.sendNotification(aproveOrder, aproveOrder.getSellerUsername(), "approved your order delivery.");
        return aproveOrder;
    }

    public Order sellerDeliverOrder(String orderId, Boolean delivered, DeliveredWorkDto deliveredWork) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update()
                .set("delivered", delivered)
                .set("status", "Delivered")
                .set("events.orderDelivered", deliveredWork.getOrderDelivered())
                .push("deliveredWork", deliveredWork);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order order = mongoTemplate.findAndModify(query, update, options, Order.class);

        if (order != null) {
            OrderMessageDto messageDetails = new OrderMessageDto();
            messageDetails.setOrderId(orderId);
            messageDetails.setBuyerUsername(order.getBuyerUsername().toLowerCase());
            messageDetails.setSellerUsername(order.getSellerUsername().toLowerCase());
            messageDetails.setTitle(order.getOffer().getGigTitle());
            messageDetails.setDescription(order.getOffer().getDescription());
            messageDetails.setSellerEmail(order.getSellerEmail().toLowerCase());
            messageDetails.setBuyerEmail(order.getBuyerEmail().toLowerCase());
            messageDetails.setOrderUrl(String.format("http://localhost:3000/orders/%s/activities", orderId));
            messageDetails.setTemplateName("order-delivered.html");
            messageDetails.setTemplateSubject("Order Deliverd");

            orderProducer.sendOrderEmailTopic(messageDetails);
            notificationService.sendNotification(order, order.getBuyerUsername(), "delivered your order.");
        }

        return order;
    }

    public Order requestDeliveryExtension(String orderId, ExtendedDeliveryDto extendedDelivery) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Order existingOrder = mongoTemplate.findOne(query, Order.class);
        if (!existingOrder.getOffer().getNewDeliveryDate().equals(extendedDelivery.getOriginalDate())) {
            throw new RuntimeException("Date not right");
        }

        Update update = new Update()
                .set("requestExtension.originalDate", extendedDelivery.getOriginalDate())
                .set("requestExtension.newDate", extendedDelivery.getNewDate())
                .set("requestExtension.days", extendedDelivery.getDays())
                .set("requestExtension.reason", extendedDelivery.getReason())
                .inc("countExtension", 1);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order order = mongoTemplate.findAndModify(query, update, options, Order.class);

        if (order != null) {
            OrderMessageDto messageDetails = new OrderMessageDto();
            messageDetails.setBuyerUsername(order.getBuyerUsername().toLowerCase());
            messageDetails.setSellerUsername(order.getSellerUsername().toLowerCase());
            messageDetails.setOriginalDate(order.getOffer().getOldDeliveryDate());
            messageDetails.setNewDate(order.getRequestExtension().getNewDate());
            messageDetails.setReason(order.getRequestExtension().getReason());
            messageDetails.setBuyerEmail(order.getBuyerEmail().toLowerCase());
            messageDetails.setSellerEmail(order.getSellerEmail().toLowerCase());
            messageDetails.setOrderUrl(String.format("http://localhost:3000/orders/%s/activities", orderId));
            messageDetails.setTemplateName("order-extension.html");
            messageDetails.setTemplateSubject("Order Extension");

            orderProducer.sendOrderEmailTopic(messageDetails);
            notificationService.sendNotification(order, order.getBuyerUsername(), "requested for an order delivery date extension.");
        }

        return order;
    }

    public Order approveDeliveryDate(String orderId, ExtendedDeliveryDto extendedDelivery) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update()
                .set("offer.deliveryInDays", extendedDelivery.getDays())
                .set("offer.newDeliveryDate", extendedDelivery.getNewDate())
                .set("offer.reason", extendedDelivery.getReason())
                .set("events.deliveryDateUpdate", extendedDelivery.getDeliveryDateUpdate())
                .set("requestExtension.originalDate", null)
                .set("requestExtension.newDate", null)
                .set("requestExtension.days", 0)
                .set("requestExtension.reason", null);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order order = mongoTemplate.findAndModify(query, update, options, Order.class);

        if (order != null) {
            OrderMessageDto messageDetails = new OrderMessageDto();
            messageDetails.setSubject("Congratulations: Your extension request was approved");
            messageDetails.setBuyerUsername(order.getBuyerUsername().toLowerCase());
            messageDetails.setSellerUsername(order.getSellerUsername().toLowerCase());
            messageDetails.setHeader("Request Accepted");
            messageDetails.setType("accepted");
            messageDetails.setMessage("You can continue working on the order.");
            messageDetails.setSellerEmail(order.getSellerEmail().toLowerCase());
            messageDetails.setBuyerEmail(order.getBuyerEmail().toLowerCase());
            messageDetails.setOrderUrl(String.format("http://localhost:3000/orders/%s/activities", orderId));
            messageDetails.setTemplateName("order-extension-approval.html");
            messageDetails.setTemplateSubject("Order Extension Approved");

            orderProducer.sendOrderEmailTopic(messageDetails);
            notificationService.sendNotification(order, order.getSellerUsername(), "approved your order delivery date extension request.");
        }

        return order;
    }

    public Order rejectDeliveryDate(String orderId, String declineReason) {
        Query query = new Query(Criteria.where("orderId").is(orderId));
        Update update = new Update()
                .set("requestExtension.originalDate", null)
                .set("requestExtension.newDate", null)
                .set("requestExtension.days", 0)
                .set("requestExtension.reason", null);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order order = mongoTemplate.findAndModify(query, update, options, Order.class);
        if (order != null) {
            OrderMessageDto messageDetails = new OrderMessageDto();
            messageDetails.setSubject("Sorry: Your extension request was rejected");
            messageDetails.setBuyerUsername(order.getBuyerUsername().toLowerCase());
            messageDetails.setSellerUsername(order.getSellerUsername().toLowerCase());
            messageDetails.setHeader("Request Rejected");
            messageDetails.setType("rejected");
            messageDetails.setSellerEmail(order.getSellerEmail().toLowerCase());
            messageDetails.setBuyerEmail(order.getBuyerEmail().toLowerCase());
            messageDetails.setMessage("You can contact the buyer for more information.");
            messageDetails.setOrderUrl(String.format("http://localhost:3000/orders/%s/activities", orderId));
            messageDetails.setTemplateName("order-extension-approval.html");
            messageDetails.setTemplateSubject("Order Extension Rejected");
            messageDetails.setDeclineReason(declineReason);

            orderProducer.sendOrderEmailTopic(messageDetails);
            notificationService.sendNotification(order, order.getSellerUsername(), "rejected your order delivery date extension request.");
        }

        return order;
    }

    public Order updateOrderReview(ReviewMessageDetailsDto reviewMessageDetails) {
        Query query = new Query(Criteria.where("orderId").is(reviewMessageDetails.getOrderId()));
        Update update = new Update();

        if ("buyer-review".equals(reviewMessageDetails.getType())) {
            update.set("buyerReview.rating", reviewMessageDetails.getRating())
                    .set("buyerReview.review", reviewMessageDetails.getReview())
                    .set("buyerReview.created", reviewMessageDetails.getCreatedAt())
                    .set("events.buyerReview", reviewMessageDetails.getCreatedAt());
        } else {
            update.set("sellerReview.rating", reviewMessageDetails.getRating())
                    .set("sellerReview.review", reviewMessageDetails.getReview())
                    .set("sellerReview.created", reviewMessageDetails.getCreatedAt())
                    .set("events.sellerReview", reviewMessageDetails.getCreatedAt());
        }

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Order order = mongoTemplate.findAndModify(query, update, options, Order.class);

        if (order != null) {
            notificationService.sendNotification(order, (reviewMessageDetails.getType().equals("buyer-review") ? order.getSellerUsername() : order.getBuyerUsername()),
                    String.format("left you a %d star review", reviewMessageDetails.getRating()));
        }

        return order;
    }
}
