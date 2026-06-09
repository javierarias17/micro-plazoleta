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
public class RestaurantModel {
    private Long id;
    private String name;
    private String nit;
    private String address;
    private String phone;
    private String urlLogo;
    private Long ownerId;
}
