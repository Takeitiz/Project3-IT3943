package com.project3.users.repository;

import com.project3.users.entity.Seller;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends MongoRepository<Seller, String> {

    Optional<Seller> findByEmail(String email);
    Optional<Seller> findById(String id);
    Optional<Seller> findByUsername(String username);

    @Aggregation(pipeline = {
            "{ $sample: { size: ?0 } }"
    })
    List<Seller> findRandomSellers(int size);
}
