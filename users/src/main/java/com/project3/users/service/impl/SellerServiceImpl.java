package com.project3.users.service.impl;

import com.github.javafaker.Faker;
import com.project3.users.dto.OrderMessageDto;
import com.project3.users.dto.ReviewMessageDetailsDto;
import com.project3.users.entity.Buyer;
import com.project3.users.entity.Seller;
import com.project3.users.exception.ResourceNotFoundException;
import com.project3.users.exception.UserAlreadyExistsException;
import com.project3.users.repository.BuyerRepository;
import com.project3.users.repository.SellerRepository;
import com.project3.users.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl {

    private final SellerRepository sellerRepository;
    private final Faker faker = new Faker();
    private final MongoTemplate mongoTemplate;
    private final BuyerServiceImpl buyerService;
    private final BuyerServiceImpl buyerServiceImpl;

    public Seller findSellerById(String sellerId) {
        Query query = new Query(Criteria.where("_id").is(sellerId));
        return mongoTemplate.findOne(query, Seller.class);
    }

    public Seller findSellerByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, Seller.class);
    }

    public Seller getSellerByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, Seller.class);
    }

    public List<Seller> getRandomSellers(int count) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sample(count)
        );

        AggregationResults<Seller> results = mongoTemplate.aggregate(
                aggregation,
                "sellers",
                Seller.class
        );

        return results.getMappedResults();
    }

    public Seller createSeller(Seller seller) {
        seller.setCreatedAt(LocalDateTime.now());
        Seller createdSeller = sellerRepository.save(seller);
        if (createdSeller.getEmail() != null) {
            buyerService.updateBuyerIsSellerProp(createdSeller.getEmail());
        }
        return createdSeller;
    }


    public void seed(int count) {
        List<Buyer> buyers = buyerServiceImpl.getRandomBuyers(count);
        for (Buyer buyer : buyers) {
            if (sellerRepository.findByEmail(buyer.getEmail()).isPresent()) {
                continue;
            }
            Seller seller = createSellerFromBuyer(buyer);
            sellerRepository.save(seller);
        }
    }

    public Seller updateSeller(String sellerId, Seller sellerData) {
        Query query = new Query(Criteria.where("_id").is(sellerId));
        Update update = new Update()
                .set("profilePublicId", sellerData.getProfilePublicId())
                .set("fullName", sellerData.getFullName())
                .set("profilePicture", sellerData.getProfilePicture())
                .set("description", sellerData.getDescription())
                .set("country", sellerData.getCountry())
                .set("skills", sellerData.getSkills())
                .set("oneliner", sellerData.getOneliner())
                .set("languages", sellerData.getLanguages())
                .set("responseTime", sellerData.getResponseTime())
                .set("experience", sellerData.getExperience())
                .set("education", sellerData.getEducation())
                .set("socialLinks", sellerData.getSocialLinks())
                .set("certificates", sellerData.getCertificates());
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query, update, options, Seller.class);
    }

    public void updateTotalGigsCount(String sellerId, int count) {
        Query query = new Query(Criteria.where("_id").is(sellerId));
        Update update = new Update().inc("totalGigs", count);
        mongoTemplate.updateFirst(query, update, Seller.class);
    }

    public void updateSellerOngoingJobsProp(String sellerId, int ongoingJobs) {
        Query query = new Query(Criteria.where("_id").is(sellerId));
        Update update = new Update().inc("ongoingJobs", ongoingJobs);
        mongoTemplate.updateFirst(query, update, Seller.class);
    }

    public void updateSellerCancelledJobsProp(String sellerId) {
        Query query = new Query(Criteria.where("_id").is(sellerId));
        Update update = new Update()
                .inc("ongoingJobs", -1)
                .inc("cancelledJobs", 1);
        mongoTemplate.updateFirst(query, update, Seller.class);
    }

    public void updateSellerCompletedJobsProp(OrderMessageDto data) {
        Query query = new Query(Criteria.where("_id").is(data.getSellerId()));

        Update update = new Update()
                .inc("ongoingJobs", data.getOngoingJobs())
                .inc("completedJobs", data.getCompletedJobs())
                .inc("totalEarnings", data.getTotalEarnings())
                .set("recentDelivery", data.getRecentDelivery());

        mongoTemplate.updateFirst(query, update, Seller.class);
    }

    public void updateSellerReview(ReviewMessageDetailsDto data) {

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

        Query query = new Query(Criteria.where("_id").is(data.getSellerId()));

        Update update = new Update()
                .inc("ratingsCount", 1)
                .inc("ratingSum", data.getRating())
                .inc("ratingCategories." + ratingKey + ".value", data.getRating())
                .inc("ratingCategories." + ratingKey + ".count", 1);

        mongoTemplate.updateFirst(query, update, Seller.class);
    }

    private Seller createSellerFromBuyer(Buyer buyer) {
        String basicDescription = String.format("%s %s %s",
                faker.commerce().productName(),
                faker.lorem().sentence(3),
                faker.commerce().material());

        Seller seller = new Seller();
        seller.setProfilePublicId(UUID.randomUUID().toString());
        seller.setFullName(faker.name().fullName());
        seller.setUsername(buyer.getUsername());
        seller.setEmail(buyer.getEmail());
        seller.setCountry(faker.address().country());
        seller.setProfilePicture(buyer.getProfilePicture());
        seller.setDescription(basicDescription.length() <= 250 ? basicDescription : basicDescription.substring(0, 250));
        seller.setOneliner(faker.lorem().sentence(faker.number().numberBetween(5, 10)));
        seller.setSkills(getRandomSkills());
        seller.setLanguages(getLanguages());
        seller.setResponseTime(faker.number().numberBetween(1, 5));
        seller.setExperience(getRandomExperiences(faker.number().numberBetween(2, 4)));
        seller.setEducation(getRandomEducations(faker.number().numberBetween(2, 4)));
        seller.setSocialLinks(Arrays.asList(
                "https://kickchatapp.com",
                "http://youtube.com",
                "https://facebook.com"
        ));
        seller.setCertificates(getCertificates());
        return seller;
    }

    private List<String> getRandomSkills() {
        List<String> allSkills = Arrays.asList(
                "Programming", "Web development", "Mobile development",
                "Proof reading", "UI/UX", "Data Science",
                "Financial modeling", "Data analysis"
        );

        int count = faker.number().numberBetween(1, 4);
        return new ArrayList<>(new HashSet<>(allSkills)).subList(0, Math.min(count, allSkills.size()));
    }

    private List<Seller.Language> getLanguages() {
        List<Seller.Language> languages = new ArrayList<>();

        Seller.Language english = new Seller.Language();
        english.setLanguage("English");
        english.setLevel("Native");

        Seller.Language spanish = new Seller.Language();
        spanish.setLanguage("Spanish");
        spanish.setLevel("Basic");

        Seller.Language german = new Seller.Language();
        german.setLanguage("German");
        german.setLevel("Basic");

        languages.addAll(Arrays.asList(english, spanish, german));
        return languages;
    }

    private List<Seller.Experience> getRandomExperiences(int count) {
        List<Seller.Experience> experiences = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Seller.Experience experience = new Seller.Experience();

            String endYear = faker.options().option("Present", "2024", "2025", "2026", "2027");
            experience.setCompany(faker.company().name());
            experience.setTitle(faker.job().title());
            experience.setStartDate(faker.options().option("2020", "2021", "2022", "2023", "2024", "2025"));
            experience.setEndDate(endYear);
            experience.setDescription(faker.lorem().sentence(100));
            experience.setCurrentlyWorkingHere("Present".equals(endYear));
            experiences.add(experience);
        }
        return experiences;
    }

    private List<Seller.Education> getRandomEducations(int count) {
        List<Seller.Education> educations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Seller.Education education = new Seller.Education();

            education.setCountry(faker.country().name());
            education.setUniversity(faker.university().name());
            education.setTitle(faker.job().title());
            education.setMajor(faker.job().field() + " " + faker.job().keySkills());
            education.setYear(String.valueOf(faker.number().numberBetween(2020, 2025)));
            educations.add(education);
        }
        return educations;
    }

    private List<Seller.Certificate> getCertificates() {
        List<Seller.Certificate> certificates = new ArrayList<>();

        Seller.Certificate cert1 = new Seller.Certificate();
        cert1.setName("Flutter App Developer");
        cert1.setFrom("Flutter Academy");
        cert1.setYear(2021);

        Seller.Certificate cert2 = new Seller.Certificate();
        cert2.setName("Android App Developer");
        cert2.setFrom("Google");
        cert2.setYear(2020);

        Seller.Certificate cert3 = new Seller.Certificate();
        cert3.setName("IOS App Developer");
        cert3.setFrom("Apple Inc.");
        cert3.setYear(2019);

        certificates.addAll(Arrays.asList(cert1, cert2, cert3));
        return certificates;
    }
}
