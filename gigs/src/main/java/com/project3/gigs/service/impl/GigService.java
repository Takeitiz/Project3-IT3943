package com.project3.gigs.service.impl;

import com.github.javafaker.Faker;
import com.project3.gigs.dto.*;
import com.project3.gigs.entity.Gig;
import com.project3.gigs.exception.ResourceNotFoundException;
import com.project3.gigs.kafka.GigProducer;
import com.project3.gigs.repository.GigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class GigService {

    private final GigRepository gigRepository;
    private final CloudinaryService cloudinaryService;
    private final MongoTemplate mongoTemplate;
    private final GigProducer gigProducer;
    private final Faker faker = new Faker();

    private static final List<String> CATEGORIES = Arrays.asList(
            "Graphics & Design",
            "Digital Marketing",
            "Writing & Translation",
            "Video & Animation",
            "Music & Audio",
            "Programming & Tech",
            "Photography",
            "Data",
            "Business"
    );

    private static final List<String> EXPECTED_DELIVERY = Arrays.asList(
            "1 Day Delivery",
            "2 Days Delivery",
            "3 Days Delivery",
            "4 Days Delivery",
            "5 Days Delivery"
    );

    private static final List<RatingDto> RANDOM_RATINGS = Arrays.asList(
            new RatingDto(20, 4),
            new RatingDto(10, 2),
            new RatingDto(20, 4),
            new RatingDto(15, 3),
            new RatingDto(5, 1)
    );

    public Gig getGigById(String gigId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(gigId));
        return mongoTemplate.findOne(query, Gig.class);
    }

    public List<Gig> getSellerGigs(String sellerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sellerId").is(sellerId).and("active").is(true));
        List<Gig> gigs =  mongoTemplate.find(query, Gig.class);
        return gigs;
    }

    public List<Gig> getSellerPausedGigs(String sellerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sellerId").is(sellerId).and("active").is(false));
        return mongoTemplate.find(query, Gig.class);
    }

    public Gig createGig(Gig gig) {
        Gig createdGig = gigRepository.save(gig);
        UpdateGigCountMessageDto messageDto = UpdateGigCountMessageDto.builder()
                .type("update-gig-count")
                .gigSellerId(gig.getSellerId())
                .count(1)
                .build();
        gigProducer.sendUpdateGigCountTopic(messageDto);
        return createdGig;
    }

    public void deleteGig(String gigId, String sellerId) {
        Query query = new Query(Criteria.where("_id").is(gigId));
        mongoTemplate.remove(query, Gig.class);
        UpdateGigCountMessageDto messageDto = UpdateGigCountMessageDto.builder()
                .type("update-gig-count")
                .gigSellerId(sellerId)
                .count(-1)
                .build();
        gigProducer.sendUpdateGigCountTopic(messageDto);
    }

    public Gig updateGig(String gigId, Gig gigData) {
        Query query = new Query(Criteria.where("_id").is(gigId));
        Update update = new Update()
                .set("title", gigData.getTitle())
                .set("description", gigData.getDescription())
                .set("categories", gigData.getCategories())
                .set("subCategories", gigData.getSubCategories())
                .set("tags", gigData.getTags())
                .set("price", gigData.getPrice())
                .set("coverImage", gigData.getCoverImage())
                .set("expectedDelivery", gigData.getExpectedDelivery())
                .set("basicTitle", gigData.getBasicTitle())
                .set("basicDescription", gigData.getBasicDescription());
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query, update, options, Gig.class);
    }

    public Gig updateActiveGigProp(String gigId, boolean gigActive) {
        Query query = new Query(Criteria.where("_id").is(gigId));
        Update update = new Update().set("active", gigActive);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query, update, options, Gig.class);
    }

    public void updateGigReview(ReviewMessageDetailsDto data) {

        Map<Integer, String> ratingTypes = Map.of(
                1, "one",
                2, "two",
                3, "three",
                4, "four",
                5, "five"
        );

        String ratingKey = ratingTypes.get(data.getRating());
        if (ratingKey == null) {
            throw new IllegalArgumentException("Invalid rating value: " + data.getRating());
        }

        Query query = new Query(Criteria.where("_id").is(data.getGigId()));

        Update update = new Update()
                .inc("ratingsCount", 1)
                .inc("ratingSum", data.getRating())
                .inc("ratingCategories." + ratingKey + ".value", data.getRating())
                .inc("ratingCategories." + ratingKey + ".count", 1);

        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);
        mongoTemplate.findAndModify(query, update, options, Gig.class);
    }

    public long getDocumentCount(String collectionName) {
        return mongoTemplate.getCollection(collectionName).countDocuments();
    }

    public void seedData(List<SellerDto> sellers, int count) {
        for (int i = 0; i < sellers.size(); i++) {
            SellerDto seller = sellers.get(i);
            String title = String.format("I will %s", faker.lorem().words(5).stream()
                    .collect(Collectors.joining(" ")));
            String basicTitle = faker.commerce().productName();
            String basicDescription = faker.lorem().sentence();
            RatingDto rating = getRandomElement(RANDOM_RATINGS);

            Gig gig = buildGig(seller, title, basicTitle, basicDescription, rating, count, i);

            UpdateGigCountMessageDto messageDto = UpdateGigCountMessageDto.builder()
                    .type("update-gig-count")
                    .gigSellerId(gig.getSellerId())
                    .count(1)
                    .build();
            gigProducer.sendUpdateGigCountTopic(messageDto);

            gigRepository.save(gig);
        }
    }

    private Gig buildGig(SellerDto seller, String title, String basicTitle,
                         String basicDescription, RatingDto rating, int count, int index) {
        return Gig.builder()
                .active(true)
                .profilePicture(seller.getProfilePicture())
                .sellerId(seller.getId())
                .email(seller.getEmail())
                .username(seller.getUsername())
                .title(truncateString(title, 80))
                .basicTitle(truncateString(basicTitle, 40))
                .basicDescription(truncateString(basicDescription, 100))
                .categories(getRandomElement(CATEGORIES))
                .subCategories(generateSubCategories())
                .description(generateDescription())
                .tags(generateTags())
                .price((double) faker.number().numberBetween(20, 30))
                .coverImage(generateCoverImage())
                .expectedDelivery(getRandomElement(EXPECTED_DELIVERY))
                .sortId(count + index + 1)
                .ratingsCount((index + 1) % 4 == 0 ? rating.getCount() : 0)
                .ratingSum((index + 1) % 4 == 0 ? rating.getSum() : 0)
                .build();
    }

    private String truncateString(String str, int maxLength) {
        return str.length() <= maxLength ? str : str.substring(0, maxLength);
    }

    private List<String> generateSubCategories() {
        return IntStream.range(0, 3)
                .mapToObj(i -> faker.commerce().department())
                .collect(Collectors.toList());
    }

    private String generateDescription() {
        return String.join(" ", faker.lorem().sentences(faker.number().numberBetween(2, 4)));
    }

    private List<String> generateTags() {
        return IntStream.range(0, 4)
                .mapToObj(i -> faker.commerce().department())
                .collect(Collectors.toList());
    }

    private String generateCoverImage() {
        return String.format("https://picsum.photos/seed/%s/200", UUID.randomUUID().toString());
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(faker.random().nextInt(list.size()));
    }

}
