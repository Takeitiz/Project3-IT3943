package com.project3.gigs.service.impl;

import com.mongodb.BasicDBObject;
import com.project3.gigs.dto.PaginateProps;
import com.project3.gigs.dto.SearchResult;
import com.project3.gigs.entity.Gig;
import com.project3.gigs.exception.ResourceNotFoundException;
import com.project3.gigs.repository.GigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final MongoTemplate mongoTemplate;
    private final GigRepository gigRepository;

    public SearchResult searchGig(String searchQuery, PaginateProps paginate, String deliveryTime, Integer min, Integer max) {
        Query query = new Query();
        Criteria textCriteria = new Criteria();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            textCriteria = new Criteria().orOperator(
                    Criteria.where("username").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("title").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("description").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("basicDescription").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("basicTitle").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("categories").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("tags").regex(".*" + searchQuery + ".*", "i"),
                    Criteria.where("subCategories").regex(".*" + searchQuery + ".*", "i")
            );
        }

        Criteria activeCriteria = Criteria.where("active").is(true);

        Criteria finalCriteria = new Criteria().andOperator(textCriteria, activeCriteria);

        if (deliveryTime != null && !deliveryTime.equals("undefined")) {
            finalCriteria = finalCriteria.and("expectedDelivery")
                    .regex(".*" + deliveryTime + ".*", "i");
        }

        if (min != null && max != null) {
            finalCriteria = finalCriteria.and("price").gte(min).lte(max);
        }

        query.addCriteria(finalCriteria);

        if (paginate.getFrom() != null && !paginate.getFrom().equals("0")) {
            query.skip(Long.parseLong(paginate.getFrom()));
        }

        query.limit(paginate.getSize());

        Sort sort = paginate.getType().equals("forward") ?
                Sort.by(Sort.Direction.ASC, "sortId") :
                Sort.by(Sort.Direction.DESC, "sortId");
        query.with(sort);

        List<Gig> gigs = mongoTemplate.find(query, Gig.class);
        long total = mongoTemplate.count(query, Gig.class);

        return new SearchResult(total, gigs);
    }

    public SearchResult searchGigsByCategory(String searchQuery) {
        Query query = new Query();

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("categories").regex(".*" + searchQuery + ".*", "i"),
                Criteria.where("active").is(true)
        );

        query.addCriteria(criteria);

        query.limit(10);

        List<Gig> gigs = mongoTemplate.find(query, Gig.class);
        long total = mongoTemplate.count(query, Gig.class);

        return new SearchResult(total, gigs);
    }


    public SearchResult getMoreGigsLikeThis(String gigId) {
        Gig referenceGig = mongoTemplate.findById(gigId, Gig.class);
        if (referenceGig == null) {
            throw new ResourceNotFoundException("Gig not found with id: " + gigId);
        }
        Query query = new Query();
        Criteria criteria = new Criteria().andOperator(
                Criteria.where("_id").ne(gigId),
                Criteria.where("categories").is(referenceGig.getCategories()),
                Criteria.where("active").is(true)
        );
        query.addCriteria(criteria);
        query.limit(10);
        List<Gig> gigs = mongoTemplate.find(query, Gig.class);
        long total = mongoTemplate.count(query, Gig.class);
        return new SearchResult(total, gigs);
    }

    public SearchResult getTopRatedGigsByCategory(String searchQuery) {
        try {
            TypedAggregation<Gig> aggregation = Aggregation.newAggregation(
                    Gig.class,
                    Aggregation.match(
                            Criteria.where("categories").regex(".*" + searchQuery + ".*", "i")
                    ),
                    Aggregation.match(
                            new Criteria().andOperator(
                                    Criteria.where("ratingSum").ne(0),
                                    Criteria.where("ratingsCount").gt(0)
                            )
                    ),
                    Aggregation.addFields()
                            .addField("averageRating")
                            .withValue(ArithmeticOperators.Divide.valueOf("ratingSum").divideBy("ratingsCount"))
                            .build(),
                    Aggregation.match(
                            Criteria.where("averageRating").is(5.0)
                    ),
                    Aggregation.limit(10)
            );

            AggregationResults<Gig> results = mongoTemplate.aggregate(
                    aggregation,
                    Gig.class
            );

            List<Gig> gigs = results.getMappedResults();
            return new SearchResult((long) gigs.size(), gigs);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching top rated gigs: " + e.getMessage(), e);
        }
    }

}
