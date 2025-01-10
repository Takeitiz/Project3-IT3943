package com.project3.users.controller;

import com.project3.users.dto.BuyerResponseDto;
import com.project3.users.entity.Buyer;
import com.project3.users.service.IBuyerService;
import com.project3.users.service.impl.BuyerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1/buyer", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerServiceImpl buyerService;

    @GetMapping("/email")
    public ResponseEntity<?> getBuyerByEmail(@RequestParam String email) {
        Buyer buyer = buyerService.fetchBuyerByEmail(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BuyerResponseDto("Buyer profile", buyer));
    }

    @GetMapping("/username")
    public ResponseEntity<?> getBuyerByCurrentUsername(@RequestParam String username) {
        Buyer buyer = buyerService.fetchBuyerByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BuyerResponseDto("Buyer profile", buyer));
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getBuyerByUsername(@PathVariable String username) {
        Buyer buyer = buyerService.fetchBuyerByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new BuyerResponseDto("Buyer profile", buyer));
    }
}
