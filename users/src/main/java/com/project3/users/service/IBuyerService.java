package com.project3.users.service;

import com.project3.users.entity.Buyer;

public interface IBuyerService {

    Buyer fetchBuyerByEmail(String email);
    Buyer fetchBuyerByUsername(String username);
}
