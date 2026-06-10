package com.pragma.powerup.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishModel {
    private Long id;
    private String name;
    private Long categoryId;
    private String description;
    private Integer price;
    private Long restaurantId;
    private String urlImage;
    private Boolean active;
}
