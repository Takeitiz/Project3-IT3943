package com.project3.gigs.dto;

import com.project3.gigs.entity.Gig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResponseDto {
    private String message;
    private Long total;
    private List<Gig> gigs;
}
