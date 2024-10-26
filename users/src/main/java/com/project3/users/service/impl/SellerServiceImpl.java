package com.project3.users.service.impl;

import com.github.javafaker.Faker;
import com.project3.users.entity.Buyer;
import com.project3.users.entity.Seller;
import com.project3.users.exception.ResourceNotFoundException;
import com.project3.users.exception.UserAlreadyExistsException;
import com.project3.users.repository.BuyerRepository;
import com.project3.users.repository.SellerRepository;
import com.project3.users.service.ISellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements ISellerService {

    private final SellerRepository sellerRepository;

    private final BuyerRepository buyerRepository;

    private final Faker faker = new Faker();

    @Override
    public Seller createSeller(Seller seller) {
        Optional<Seller> optionalSeller = sellerRepository.findByEmail(seller.getEmail());
        if (optionalSeller.isPresent()) {
            throw new UserAlreadyExistsException("User already registered with given email " + seller.getEmail());
        }
        return sellerRepository.save(seller);
    }

    @Override
    public Seller findSellerById(String id) {
        return sellerRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Seller", "sellerId", id)
        );
    }

    @Override
    public Seller findSellerByUsername(String username) {
        return sellerRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Seller", "username", username)
        );
    }

    @Override
    public List<Seller> getRandomSellers(int size) {
        return sellerRepository.findRandomSellers(size);
    }

    @Override
    public void seed(int count) {
        List<Buyer> buyers = buyerRepository.findRandomBuyers(count);
        for (Buyer buyer : buyers) {
            if (sellerRepository.findByEmail(buyer.getEmail()).isPresent()) {
                continue;
            }
            Seller seller = createSellerFromBuyer(buyer);
            sellerRepository.save(seller);
        }
    }

    @Override
    public Seller updateSeller(String sellerId, Seller updatedSeller) {
        Seller existingSeller = findSellerById(sellerId);

        if (updatedSeller.getProfilePublicId() != null) {
            existingSeller.setProfilePublicId(updatedSeller.getProfilePublicId());
        }
        if (updatedSeller.getFullname() != null) {
            existingSeller.setFullname(updatedSeller.getFullname());
        }
        if (updatedSeller.getProfilePicture() != null) {
            existingSeller.setProfilePicture(updatedSeller.getProfilePicture());
        }
        if (updatedSeller.getDescription() != null) {
            existingSeller.setDescription(updatedSeller.getDescription());
        }
        if (updatedSeller.getOneliner() != null) {
            existingSeller.setOneliner(updatedSeller.getOneliner());
        }
        if (updatedSeller.getCountry() != null) {
            existingSeller.setCountry(updatedSeller.getCountry());
        }
        if (updatedSeller.getSkills() != null) {
            existingSeller.setSkills(updatedSeller.getSkills());
        }
        if (updatedSeller.getLanguages() != null) {
            existingSeller.setLanguages(updatedSeller.getLanguages());
        }
        if (updatedSeller.getResponseTime() != null) {
            existingSeller.setResponseTime(updatedSeller.getResponseTime());
        }
        if (updatedSeller.getExperience() != null) {
            existingSeller.setExperience(updatedSeller.getExperience());
        }
        if (updatedSeller.getEducation() != null) {
            existingSeller.setEducation(updatedSeller.getEducation());
        }
        if (updatedSeller.getSocialLinks() != null) {
            existingSeller.setSocialLinks(updatedSeller.getSocialLinks());
        }
        if (updatedSeller.getCertificates() != null) {
            existingSeller.setCertificates(updatedSeller.getCertificates());
        }

        return sellerRepository.save(existingSeller);
    }

    private Seller createSellerFromBuyer(Buyer buyer) {
        String basicDescription = String.format("%s %s %s",
                faker.commerce().productName(),
                faker.lorem().sentence(3),
                faker.commerce().material());

        Seller seller = new Seller();
        seller.setProfilePublicId(UUID.randomUUID().toString());
        seller.setFullname(faker.name().fullName());
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
            experience.setDescription(faker.lorem().sentence().substring(0, 100));
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
