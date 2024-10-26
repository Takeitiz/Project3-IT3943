package com.project3.users.service.impl;

import com.project3.users.entity.Buyer;
import com.project3.users.exception.ResourceNotFoundException;
import com.project3.users.repository.BuyerRepository;
import com.project3.users.service.IBuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements IBuyerService {

    private final BuyerRepository buyerRepository;

    @Override
    public Buyer fetchBuyerByEmail(String email) {
        return buyerRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Buyer", "email", email)
        );
    }

    @Override
    public Buyer fetchBuyerByUsername(String username) {
        return buyerRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("Buyer", "username", username)
        );
    }

}
