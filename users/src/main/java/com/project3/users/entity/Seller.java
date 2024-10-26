package com.project3.users.entity;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "sellers")
@AllArgsConstructor
@NoArgsConstructor
public class Seller {

    @Id
    private String id;

    @NotBlank(message = "Fullname is required")
    private String fullname;

    @Indexed
    private String username;

    @Indexed
    private String email;

    @NotBlank(message = "Profile picture is required")
    private String profilePicture;

    @NotBlank(message = "Seller description is required")
    private String description;

    private String profilePublicId;

    @NotBlank(message = "Oneliner field is required")
    private String oneliner = "";

    @NotBlank(message = "Country field is required")
    private String country;

    @NotEmpty(message = "Languages are required")
    private List<@Valid Language> languages;

    @NotEmpty(message = "Skills are required")
    private List<@NotBlank(message = "Please add at least one skill") String> skills;

    private Integer ratingsCount = 0;
    private Integer ratingSum = 0;
    private RatingCategories ratingCategories = new RatingCategories();

    @NotNull(message = "Response time is required")
    @Min(value = 1, message = "Response time must be greater than zero")
    private Integer responseTime = 0;

    private LocalDateTime recentDelivery;

    @NotEmpty(message = "Experience is required")
    private List<Experience> experience;

    @NotEmpty(message = "Education is required")
    private List<@Valid Education> education;


    private List<String> socialLinks;

    private List<@Valid Certificate> certificates;

    private Integer ongoingJobs = 0;
    private Integer completedJobs = 0;
    private Integer cancelledJobs = 0;
    private Double totalEarnings = 0.0;
    private Integer totalGigs = 0;

    @CreatedDate
    private LocalDateTime createdAt;

    @Data
    public static class Language {
        @NotBlank(message = "Language is required")
        private String language;
        @NotBlank(message = "Level is required")
        private String level;
    }

    @Data
    public static class RatingCategories {
        private RatingCategory five = new RatingCategory();
        private RatingCategory four = new RatingCategory();
        private RatingCategory three = new RatingCategory();
        private RatingCategory two = new RatingCategory();
        private RatingCategory one = new RatingCategory();
    }

    @Data
    public static class RatingCategory {
        private Integer value = 0;
        private Integer count = 0;
    }

    @Data
    public static class Experience {
        @NotBlank(message = "Company is required")
        private String company = "";

        @NotBlank(message = "Title is required")
        private String title = "";

        @NotBlank(message = "Start date is required")
        private String startDate = "";

        @NotBlank(message = "End date is required")
        private String endDate = "";

        private String description = "";

        @NotNull(message = "Currently working here status is required")
        private Boolean currentlyWorkingHere = false;
    }

    @Data
    public static class Education {
        @NotBlank(message = "Country is required")
        private String country = "";

        @NotBlank(message = "University is required")
        private String university = "";

        @NotBlank(message = "Title is required")
        private String title = "";

        @NotBlank(message = "Major is required")
        private String major = "";

        @NotBlank(message = "Year is required")
        private String year = "";
    }

    @Data
    public static class Certificate {
        @NotBlank(message = "Certificate name is required")
        private String name;

        @NotBlank(message = "Certificate from is required")
        private String from;

        @NotNull(message = "Year is required")
        private Integer year;
    }
}
