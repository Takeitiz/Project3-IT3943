package com.project3.users.controller;

import com.project3.users.entity.Buyer;
import com.project3.users.service.IBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/buyer", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class BuyerController {

    private final IBuyerService ibuyerService;

    @GetMapping("/email")
    public ResponseEntity<Buyer> getBuyerByEmail(@RequestParam String email) {
        Buyer buyer = ibuyerService.fetchBuyerByEmail(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(buyer);
    }

    @GetMapping("/user")
    public ResponseEntity<Buyer> getBuyerByCurrentUsername(@RequestParam String username) {
        Buyer buyer = ibuyerService.fetchBuyerByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(buyer);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Buyer> getBuyerByUsername(@PathVariable String username) {
        Buyer buyer = ibuyerService.fetchBuyerByUsername(username);
        return ResponseEntity.status(HttpStatus.OK)
                .body(buyer);
    }
}
