package com.project3.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBuyerGigDto {
    private String buyerId;
    private String purchasedGigId;
    private String type;
}
