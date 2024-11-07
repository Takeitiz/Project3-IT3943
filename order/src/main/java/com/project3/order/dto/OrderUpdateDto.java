package com.project3.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderUpdateDto {
    @NotNull
    private String originalDate;

    @NotNull
    private String newDate;

    @NotNull
    private Integer days;

    @NotNull
    private String reason;

    private String deliveryDateUpdate;
}
