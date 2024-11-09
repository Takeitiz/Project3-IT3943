package com.project3.order.service;

import com.mongodb.client.MongoClient;
import com.project3.order.entity.Order;
import com.project3.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;

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
}
