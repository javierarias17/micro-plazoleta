package com.pragma.powerup.factory;

import com.pragma.powerup.domain.model.RestaurantModel;

public class RestaurantModelFactory {

    private RestaurantModelFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static RestaurantModel createValidRestaurant() {
        return RestaurantModel.builder()
                .name("La Brasa")
                .nit("900123456")
                .address("Calle 10 # 5-23, Medellín")
                .phone("+573001234567")
                .urlLogo("https://example.com/logo.png")
                .ownerId(1L)
                .build();
    }

    public static RestaurantModel createSavedRestaurant() {
        return RestaurantModel.builder()
                .id(1L)
                .name("La Brasa")
                .nit("900123456")
                .address("Calle 10 # 5-23, Medellín")
                .phone("+573001234567")
                .urlLogo("https://example.com/logo.png")
                .ownerId(1L)
                .build();
    }
}
