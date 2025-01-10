package com.project3.gigs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private Integer ratingsCount = 0;
    private Integer ratingSum = 0;

    private RatingCategories ratingCategories = new RatingCategories();
    private Double price = 0.0;
    private Integer sortId;
    private String coverImage;
    private LocalDateTime createdAt;

    @Transient
    private Double similarityScore;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
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

