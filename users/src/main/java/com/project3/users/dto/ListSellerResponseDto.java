package com.project3.users.dto;

import com.project3.users.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListSellerResponseDto {
    private String message;
    private List<Seller> sellers;
}
