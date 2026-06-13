package com.pragma.powerup.infrastructure.out.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantSummaryDto {
    private Long id;
    private String name;
    private String urlLogo;
}
