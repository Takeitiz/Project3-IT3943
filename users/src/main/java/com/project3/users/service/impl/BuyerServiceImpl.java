package com.project3.users.service.impl;

import com.mongodb.client.MongoClient;
import com.project3.users.dto.OrderMessageDto;
import com.project3.users.dto.UpdateBuyerGigDto;
import com.project3.users.entity.Buyer;
import com.project3.users.exception.ResourceNotFoundException;
import com.project3.users.repository.BuyerRepository;
import com.project3.users.service.IBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements IBuyerService {

    private final BuyerRepository buyerRepository;
    private final MongoTemplate mongoTemplate;
    private final MongoClient mongo;

    @Override
    public Buyer fetchBuyerByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, Buyer.class);
    }

    @Override
    public Buyer fetchBuyerByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        Buyer buyer = mongoTemplate.findOne(query, Buyer.class);
        return buyer;
    }

    public List<Buyer> getRandomBuyers(int count) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sample(count)
        );

        AggregationResults<Buyer> results = mongoTemplate.aggregate(
                aggregation,
                "buyers",
                Buyer.class
        );

        return results.getMappedResults();
    }

    public void createBuyer(Buyer buyer) {
        Buyer existingBuyer = fetchBuyerByEmail(buyer.getEmail());
        if (existingBuyer == null) {
            mongoTemplate.save(buyer);
        }
    }

    public void updateBuyerIsSellerProp(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        Update update = new Update().set("isSeller", true);

        mongoTemplate.updateFirst(query, update, Buyer.class);
    }

    public void updateBuyerPurchasedGigsProp(OrderMessageDto orderMessageDto) {
        Query query = new Query(Criteria.where("_id").is(orderMessageDto.getBuyerId()));
        Update update;

        if ("purchased-gigs".equals(orderMessageDto.getType())) {
            update = new Update().push("purchasedGigs", orderMessageDto.getPurchasedGigs());
        } else {
            update = new Update().pull("purchasedGigs", orderMessageDto.getPurchasedGigs());
        }

        mongoTemplate.updateFirst(query, update, Buyer.class);
    }

}
