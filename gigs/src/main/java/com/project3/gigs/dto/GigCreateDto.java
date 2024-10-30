package com.project3.gigs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GigCreateDto {
    @NotBlank(message = "Seller Id is required")
    private String sellerId;

    @NotBlank(message = "Profile picture is required")
    private String profilePicture;

    @NotBlank(message = "Gig title is required")
    private String title;

    @NotBlank(message = "Gig description is required")
    private String description;

    @NotBlank(message = "Gig category is required")
    private String categories;

    @NotEmpty(message = "Please add at least one subcategory")
    private List<String> subCategories;

    @NotEmpty(message = "Please add at least one tag")
    private List<String> tags;

    @NotNull(message = "Gig price is required")
    @DecimalMin(value = "5.00", message = "Gig price must be greater than $4.99")
    private Double price;

    @NotBlank(message = "Gig cover image is required")
    private String coverImage;

    @NotBlank(message = "Gig expected delivery is required")
    private String expectedDelivery;

    @NotBlank(message = "Gig basic title is required")
    private String basicTitle;

    @NotBlank(message = "Gig basic description is required")
    private String basicDescription;

    private String username;

    private String email;
}
