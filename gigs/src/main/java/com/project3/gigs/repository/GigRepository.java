package com.project3.gigs.repository;

import com.project3.gigs.entity.Gig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GigRepository extends MongoRepository<Gig, String> {


    @Query("{'sellerId': ?0, 'active':  false}")
    List<Gig> findInactiveGigsBySellerId(String sellerId);

    @Query("{'sellerId': ?0, 'active':  true}")
    List<Gig> findActiveGigsBySellerId(String sellerId);

    @Query("{ 'categories': ?0, 'active': true }")
    List<Gig> findGigsByCategory(String category);

    @Query(value = "{ 'categories': ?0, 'active': true }", sort = "{ 'ratingSum': -1 }")
    List<Gig> findTopRatedByCategory(String category);
}
