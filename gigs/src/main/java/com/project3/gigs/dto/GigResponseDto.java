package com.project3.gigs.dto;

import com.project3.gigs.entity.Gig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GigResponseDto {
    private String message;
    private Gig gig;
}
