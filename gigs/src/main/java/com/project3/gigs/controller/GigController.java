package com.project3.gigs.controller;

import com.project3.gigs.dto.GigCreateDto;
import com.project3.gigs.dto.GigUpdateDto;
import com.project3.gigs.dto.ResponseDto;
import com.project3.gigs.entity.Gig;
import com.project3.gigs.service.impl.CloudinaryService;
import com.project3.gigs.service.impl.GigServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/api/gig", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class GigController {

    private final GigServiceImpl gigServiceImpl;
    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/create")
    public ResponseEntity<Gig> createGig(@Valid @RequestBody GigCreateDto gigCreateDto)  {
        Gig Gig = gigServiceImpl.createGig(gigCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Gig);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map> uploadGigImage(@RequestParam("coverImage") MultipartFile coverImage)  {
        Map uploadfile = cloudinaryService.uploadFile(coverImage);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(uploadfile);
    }

    @DeleteMapping("/{gigId}/{sellerId}")
    public ResponseEntity<ResponseDto> deleteGig(@PathVariable String gigId, @PathVariable String sellerId) {
        boolean isDeleted = gigServiceImpl.delete(gigId, sellerId);
        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("200", "Gig deleted successfully"));
        }else{
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto("417", "Delete operation failed. Please try again or contact Dev team"));
        }
    }

    @GetMapping("/{gigId}")
    public  ResponseEntity<Gig> getGigById(@PathVariable String gigId) {
        Gig gig = gigServiceImpl.getGigById(gigId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(gig);
    }

    @GetMapping("/seller/{sellerId")
    public  ResponseEntity<List<Gig>> getGigBySellerId(@PathVariable String sellerId) {
        List<Gig> gigs = gigServiceImpl.getSellerGigs(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(gigs);
    }

    @GetMapping("/seller/pause/{sellerId}")
    public  ResponseEntity<List<Gig>> getSellerInactiveGigs(@PathVariable String sellerId) {
        List<Gig> gigs = gigServiceImpl.getInactiveGigsBySellerId(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(gigs);
    }

//    @GetMapping("/top/{username}")
//    public  ResponseEntity<List<Gig>> getTopGigsByUsername(@PathVariable String username) {
//
//    }
//
//    @GetMapping("/category/{username}")
//    public  ResponseEntity<List<Gig>> getGigByUsername(@PathVariable String username) {}

    @PutMapping("/{gigId}")
    public ResponseEntity<Gig> updateGig(@Valid @RequestBody GigUpdateDto gigUpdateDTO, @PathVariable String gigId) {
        Gig gig = gigServiceImpl.updateGig(gigUpdateDTO, gigId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(gig);
    }

    @PutMapping("/active/{gigId}")
    public ResponseEntity<Gig> updateActiveGig(@PathVariable String gigId, @RequestBody boolean gigActive) {
        Gig gig = gigServiceImpl.updateActiveGig(gigId, gigActive);
        return ResponseEntity.status(HttpStatus.OK)
                .body(gig);
    }
}
