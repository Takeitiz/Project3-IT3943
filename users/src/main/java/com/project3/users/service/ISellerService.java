package com.project3.users.service;


import com.project3.users.entity.Seller;

import java.util.List;

public interface ISellerService {

    public Seller createSeller(Seller seller);

    public Seller findSellerById(String id);

    public Seller findSellerByUsername(String username);

    public List<Seller> getRandomSellers(int size);

    public void seed(int count);

    public Seller updateSeller(String sellerId, Seller seller);
}
