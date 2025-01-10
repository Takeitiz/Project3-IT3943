package com.project3.order.dto;

import com.project3.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListOrderResponseDto {
    private String message;
    private List<Order> orders;
}
