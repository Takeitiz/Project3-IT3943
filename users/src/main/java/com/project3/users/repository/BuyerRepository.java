package com.project3.users.repository;

import com.project3.users.entity.Buyer;
import com.project3.users.entity.Seller;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuyerRepository extends MongoRepository<Buyer, String> {

    Optional<Buyer> findByEmail(String email);
    Optional<Buyer> findByUsername(String username);

    @Aggregation(pipeline = {
            "{ $sample: { size: ?0 } }"
    })
    List<Buyer> findRandomBuyers(int size);
}
