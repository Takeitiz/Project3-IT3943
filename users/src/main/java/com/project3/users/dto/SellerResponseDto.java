package com.project3.users.dto;

import com.project3.users.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SellerResponseDto {
    private String message;
    private Seller seller;
}
