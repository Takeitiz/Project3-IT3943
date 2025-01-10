package com.project3.gigs.controller;

import com.project3.gigs.dto.*;
import com.project3.gigs.entity.Gig;
import com.project3.gigs.kafka.GigProducer;
import com.project3.gigs.redis.GigCacheService;
import com.project3.gigs.service.impl.CloudinaryService;
import com.project3.gigs.service.impl.GigService;
import com.project3.gigs.service.impl.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path="/api/v1/gig", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class GigController {

    private final GigService gigServiceImpl;
    private final CloudinaryService cloudinaryService;
    private final SearchService searchService;
    private final GigCacheService gigCacheService;
    private final GigProducer gigProducer;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createGig(@RequestBody Gig gig)  {

        String profilePublicId = UUID.randomUUID().toString();
        Map uploadResult = cloudinaryService.upload(gig.getCoverImage(), profilePublicId, true, true);
        if (uploadResult.get("public_id") == null) {
            ResponseEntity.badRequest().body("File upload error. Try again.', 'Create gig() method");
        }
        gig.setCoverImage((String) uploadResult.get("secure_url"));
        long count = gigServiceImpl.getDocumentCount("gigs");
        gig.setSortId((int) count + 1);
        Gig createdGig = gigServiceImpl.createGig(gig);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GigResponseDto("Gig created successfully.", createdGig));
    }

    @PutMapping("/{gigId}")
    public ResponseEntity<?> updateGig(@RequestBody Gig gigData, @PathVariable String gigId) {

        boolean isDataUrl = isDataUrl(gigData.getCoverImage());
        String coverImage = "";
        if (isDataUrl) {
            Map uploadResult = cloudinaryService.upload(gigData.getCoverImage(), null, null, null);
            if (uploadResult.get("public_id") == null) {
                ResponseEntity.badRequest().body("File upload error. Try again.', 'Create gig() method");
            }
            coverImage = (String) uploadResult.get("secure_url");
        } else {
            coverImage = gigData.getCoverImage();
        }
        gigData.setCoverImage(coverImage);
        Gig gig = gigServiceImpl.updateGig(gigId, gigData);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GigResponseDto("Gig updated successfully.", gig));
    }

    @DeleteMapping("/{gigId}/{sellerId}")
    public ResponseEntity<?> deleteGig(@PathVariable String gigId, @PathVariable String sellerId) {
        gigServiceImpl.deleteGig(gigId, sellerId);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto("Gig deleted successfully."));
    }

    @PutMapping("/active/{gigId}")
    public ResponseEntity<?> updateActiveGig(@PathVariable String gigId, @RequestBody GigActiveRequestDto gigActiveRequest) {
        Gig gig = gigServiceImpl.updateActiveGigProp(gigId, gigActiveRequest.isActive());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GigResponseDto("Gig updated successfully.", gig));
    }

    @GetMapping("/{gigId}")
    public  ResponseEntity<?> getGigById(@PathVariable String gigId) {
        Gig gig = gigServiceImpl.getGigById(gigId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GigResponseDto("Get gig by id", gig));
    }

    @GetMapping("/seller/{sellerId}")
    public  ResponseEntity<?> getGigBySellerId(@PathVariable String sellerId) {
        List<Gig> gigs = gigServiceImpl.getSellerGigs(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ListGigResponseDto("Seller gigs", gigs));
    }

    @GetMapping("/seller/pause/{sellerId}")
    public  ResponseEntity<?> getSellerInactiveGigs(@PathVariable String sellerId) {
        List<Gig> gigs = gigServiceImpl.getSellerPausedGigs(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ListGigResponseDto("Seller gigs", gigs));
    }

    @GetMapping("/search/{from}/{size}/{type}")
    public  ResponseEntity<?> searchGigs(
            @PathVariable String from,
            @PathVariable int size,
            @PathVariable String type,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String delivery_time,
            @RequestParam(required = false) Integer minprice,
            @RequestParam(required = false) Integer maxprice
            ) {
        PaginateProps paginate = new PaginateProps(from, size, type);
        SearchResult searchResult = searchService.searchGig(
                query,
                paginate,
                delivery_time,
                minprice,
                maxprice
        );
        if ("backward".equals(type)) {
            searchResult.getHits().sort(Comparator.comparing(Gig::getSortId));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SearchResponseDto("Search gigs results", searchResult.getTotal(), searchResult.getHits()));
    }

    @GetMapping("/top/{username}")
    public  ResponseEntity<?> getTopGigsByUsername(@PathVariable String username) {
        String category = gigCacheService.getUserSelectedCategory("selectedCategories:" + username);
        SearchResult searchResult = searchService.getTopRatedGigsByCategory(category);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SearchResponseDto("Search top gigs results", searchResult.getTotal(), searchResult.getHits()));
    }

    @GetMapping("/category/{username}")
    public ResponseEntity<?> gigsByCategory(@PathVariable String username) {
        String category = gigCacheService.getUserSelectedCategory("selectedCategories:" + username);
        SearchResult searchResult = searchService.searchGigsByCategory(category);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SearchResponseDto("Search gigs category results", searchResult.getTotal(), searchResult.getHits()));
    }

    @GetMapping("/similar/{gigId}")
    public ResponseEntity<?> similarGig(@PathVariable String gigId) {
        SearchResult searchResult = searchService.getMoreGigsLikeThis(gigId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SearchResponseDto("More gigs like this result", searchResult.getTotal(), searchResult.getHits()));
    }

    @PutMapping("/seed/{count}")
    public ResponseEntity<?> seed(@PathVariable int count) {
        gigProducer.sendGetSellersTopic(count);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponseDto("Seed gigs successfully."));
    }

    private boolean isDataUrl(String str) {
        return str != null && str.startsWith("data:");
    }
}
