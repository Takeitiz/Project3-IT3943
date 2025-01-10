package com.project3.users.controller;

import com.project3.users.dto.ListSellerResponseDto;
import com.project3.users.dto.MessageResponseDto;
import com.project3.users.dto.ResponseDto;
import com.project3.users.dto.SellerResponseDto;
import com.project3.users.entity.Seller;
import com.project3.users.service.ISellerService;
import com.project3.users.service.impl.SellerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1/seller", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class SellerController {

    private final SellerServiceImpl sellerService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Seller seller) {
        Seller existingSeller = sellerService.getSellerByEmail(seller.getEmail());
        if (existingSeller != null) {
            return ResponseEntity.badRequest().body("Seller already exist. Go to your account page to update.', 'Create seller() method error");
        }
        Seller createdSeller = sellerService.createSeller(seller);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SellerResponseDto("Seller created successfully.", createdSeller));
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<?> updateSeller(@PathVariable("sellerId") String sellerId, @RequestBody Seller seller) {
        Seller updatedSeller = sellerService.updateSeller(sellerId, seller);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SellerResponseDto("Seller created successfully.", updatedSeller));
    }

    @GetMapping("/id/{sellerId}")
    public ResponseEntity<?> getSellerById(@PathVariable("sellerId") String sellerId) {
        Seller seller = sellerService.findSellerById(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SellerResponseDto("Seller profile.", seller));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getSellerByUsername(@PathVariable("username") String username) {
        Seller seller = sellerService.findSellerByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SellerResponseDto("Seller profile.", seller));
    }

    @GetMapping("/random/{size}")
    public ResponseEntity<?> getRandomSeller(@PathVariable int size) {
        List<Seller> sellers = sellerService.getRandomSellers(size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ListSellerResponseDto("Random sellers profile", sellers));
    }

    @PutMapping("/seed/{count}")
    public ResponseEntity<?> seed(@PathVariable("count") int count) {
        sellerService.seed(count);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponseDto("Sellers created successfully"));
    }


}
