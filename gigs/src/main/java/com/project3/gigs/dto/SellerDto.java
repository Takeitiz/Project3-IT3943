package com.project3.gigs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerDto {

    private String id;
    private String fullName;
    private String username;
    private String email;
    private String profilePicture;
    private String description;
    private String profilePublicId;
    private String oneliner = "";
    private String country;
    private List<Language> languages;
    private List<String> skills;
    private Integer ratingsCount = 0;
    private Integer ratingSum = 0;
    private RatingCategories ratingCategories = new RatingCategories();
    private Integer responseTime = 0;
    private LocalDateTime recentDelivery;
    private List<Experience> experience;
    private List<Education> education;
    private List<String> socialLinks;
    private List<Certificate> certificates;
    private Integer ongoingJobs = 0;
    private Integer completedJobs = 0;
    private Integer cancelledJobs = 0;
    private Double totalEarnings = 0.0;
    private Integer totalGigs = 0;
    @CreatedDate
    private LocalDateTime createdAt;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Language {
        private String language;
        private String level;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RatingCategories {
        private RatingCategory five = new RatingCategory();
        private RatingCategory four = new RatingCategory();
        private RatingCategory three = new RatingCategory();
        private RatingCategory two = new RatingCategory();
        private RatingCategory one = new RatingCategory();
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RatingCategory {
        private Integer value = 0;
        private Integer count = 0;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Experience {
        private String company = "";
        private String title = "";
        private String startDate = "";
        private String endDate = "";
        private String description = "";
        private Boolean currentlyWorkingHere = false;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Education {
        private String country = "";
        private String university = "";
        private String title = "";
        private String major = "";
        private String year = "";
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Certificate {
        private String name;
        private String from;
        private Integer year;
    }
}
