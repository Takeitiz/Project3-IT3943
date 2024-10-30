package com.project3.gigs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "gigs")
public class Gig {
    @Id
    private String id;

    @Indexed
    private String sellerId;
    private String username;
    private String profilePicture;
    private String email;
    private String title;
    private String description;
    private String basicTitle;
    private String basicDescription;
    private String categories;
    private List<String> subCategories;
    private List<String> tags;
    private boolean active = true;
    private String expectedDelivery = "";
    private int ratingsCount = 0;
    private int ratingSum = 0;

    private RatingCategories ratingCategories = new RatingCategories();
    private double price = 0.0;
    private int sortId;
    private String coverImage;
    private LocalDateTime createdAt;

    @Data
    public static class RatingCategory {
        private int value = 0;
        private int count = 0;
    }

    @Data
    public static class RatingCategories {
        private RatingCategory five = new RatingCategory();
        private RatingCategory four = new RatingCategory();
        private RatingCategory three = new RatingCategory();
        private RatingCategory two = new RatingCategory();
        private RatingCategory one = new RatingCategory();
    }
}

