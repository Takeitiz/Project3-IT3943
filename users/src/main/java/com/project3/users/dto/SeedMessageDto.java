package com.project3.users.dto;

import com.project3.users.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeedMessageDto {
    private List<Seller> sellers;
    private Integer count;
}
