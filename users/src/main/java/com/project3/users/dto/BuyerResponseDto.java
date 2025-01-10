package com.project3.users.dto;

import com.project3.users.entity.Buyer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BuyerResponseDto {
    private String message;
    private Buyer buyer;
}
