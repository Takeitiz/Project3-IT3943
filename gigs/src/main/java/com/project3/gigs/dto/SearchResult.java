package com.project3.gigs.dto;

import com.project3.gigs.entity.Gig;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResult {
    private Long total;
    private List<Gig> hits;
}
