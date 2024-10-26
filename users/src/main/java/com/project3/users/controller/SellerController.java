package com.project3.users.controller;

import com.project3.users.dto.ResponseDto;
import com.project3.users.entity.Seller;
import com.project3.users.service.ISellerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/seller", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class SellerController {

    private final ISellerService iSellerService;

    @PostMapping("/create")
    public ResponseEntity<Seller> create(@Valid @RequestBody Seller seller) {
        Seller createdSeller = iSellerService.createSeller(seller);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdSeller);
    }

    @GetMapping("/id/{sellerId}")
    public ResponseEntity<Seller> getSellerById(@PathVariable("sellerId") String sellerId) {
        Seller seller = iSellerService.findSellerById(sellerId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(seller);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Seller> getSellerByUsername(@PathVariable("username") String username) {
        Seller seller = iSellerService.findSellerByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(seller);
    }

    @GetMapping("/random")
    public ResponseEntity<List<Seller>> getRandomSeller(@RequestParam int size) {
        List<Seller> sellers = iSellerService.getRandomSellers(size);
        return ResponseEntity.status(HttpStatus.OK)
                .body(sellers);
    }

    @PutMapping("/seed/{count}")
    public ResponseEntity<ResponseDto> seed(@PathVariable("count") int count) {
        iSellerService.seed(count);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201", "Sellers created successfully"));
    }

    @PutMapping("/{sellerId}")
    public ResponseEntity<Seller> updateSeller(@PathVariable("sellerId") String sellerId, @RequestBody Seller seller) {
        Seller updatedSeller = iSellerService.updateSeller(sellerId, seller);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedSeller);
    }

}
