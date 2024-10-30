package com.project3.gigs.service.impl;

import com.project3.gigs.dto.GigCreateDto;
import com.project3.gigs.dto.GigUpdateDto;
import com.project3.gigs.entity.Gig;
import com.project3.gigs.exception.ResourceNotFoundException;
import com.project3.gigs.repository.GigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GigServiceImpl {

    private final GigRepository gigRepository;
    private final CloudinaryService cloudinaryService;

    public Gig createGig(GigCreateDto gigCreateDto) {

        Gig gig = new Gig();
        gig.setSellerId(gigCreateDto.getSellerId());
        gig.setUsername(gigCreateDto.getUsername());
        gig.setEmail(gigCreateDto.getEmail());
        gig.setProfilePicture(gigCreateDto.getProfilePicture());
        gig.setTitle(gigCreateDto.getTitle());
        gig.setDescription(gigCreateDto.getDescription());
        gig.setCategories(gigCreateDto.getCategories());
        gig.setSubCategories(gigCreateDto.getSubCategories());
        gig.setTags(gigCreateDto.getTags());
        gig.setPrice(gigCreateDto.getPrice());
        gig.setExpectedDelivery(gigCreateDto.getExpectedDelivery());
        gig.setBasicTitle(gigCreateDto.getBasicTitle());
        gig.setBasicDescription(gigCreateDto.getBasicDescription());
        gig.setCoverImage(gigCreateDto.getCoverImage());

        return gigRepository.save(gig);
    }

    public boolean delete(String gigId, String userId) {
        gigRepository.deleteById(gigId);

        // Add even to user to delete gig by userID.
        return true;
    }

    public Gig getGigById(String gigId) {
        Gig gig = gigRepository.findById(gigId).orElseThrow(
                () -> new ResourceNotFoundException("Gig", "GigId", gigId)
        );
        return gig;
    }

    public List<Gig> getSellerGigs(String sellerId) {
        return gigRepository.findActiveGigsBySellerId(sellerId);
    }

    public List<Gig> getInactiveGigsBySellerId(String sellerId) {
        return gigRepository.findInactiveGigsBySellerId(sellerId);
    }

    public Gig updateGig(GigUpdateDto gigUpdateDto, String gigId) {
        Gig gig = gigRepository.findById(gigId).orElseThrow(
                () -> new ResourceNotFoundException("Gig", "GigId", gigId)
        );

        if (gigUpdateDto.getTitle() != null)
            gig.setTitle(gigUpdateDto.getTitle());
        if (gigUpdateDto.getDescription() != null)
            gig.setDescription(gigUpdateDto.getDescription());
        if (gigUpdateDto.getSubCategories() != null)
            gig.setCategories(gigUpdateDto.getCategories());
        if (gigUpdateDto.getSubCategories() != null)
            gig.setSubCategories(gigUpdateDto.getSubCategories());
        if (gigUpdateDto.getTags() != null)
            gig.setTags(gigUpdateDto.getTags());
        if (gigUpdateDto.getPrice() != null)
            gig.setPrice(gigUpdateDto.getPrice());
        if (gigUpdateDto.getExpectedDelivery() != null)
            gig.setExpectedDelivery(gigUpdateDto.getExpectedDelivery());
        if (gigUpdateDto.getBasicTitle() != null)
            gig.setBasicTitle(gigUpdateDto.getBasicTitle());
        if (gigUpdateDto.getBasicDescription() != null)
            gig.setBasicDescription(gigUpdateDto.getBasicDescription());
        if (gigUpdateDto.getCoverImage() != null)
            gig.setCoverImage(gigUpdateDto.getCoverImage());

        return gigRepository.save(gig);
    }

    public Gig updateActiveGig(String gigId, boolean active) {
        Gig gig = gigRepository.findById(gigId).orElseThrow(
                () -> new ResourceNotFoundException("Gig", "GigId", gigId)
        );
        gig.setActive(active);
        return gigRepository.save(gig);
    }
}
