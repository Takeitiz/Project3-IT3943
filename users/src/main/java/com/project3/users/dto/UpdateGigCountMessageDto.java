package com.project3.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class UpdateGigCountMessageDto {
    private String type;
    private String gigSellerId;
    private Integer count;
}
