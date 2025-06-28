package com.immobile.real_estate_backend.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentCollectionDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalAmount;
    private String currency;
}
