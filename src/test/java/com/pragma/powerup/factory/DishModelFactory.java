package com.pragma.powerup.factory;

import com.pragma.powerup.domain.model.DishModel;

public class DishModelFactory {

    private DishModelFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static DishModel createValidDish() {
        return DishModel.builder()
                .name("Bandeja Paisa")
                .categoryId(1L)
                .description("Traditional Colombian dish with beans, rice, meat and more")
                .price(25000)
                .restaurantId(1L)
                .urlImage("https://example.com/bandeja-paisa.jpg")
                .build();
    }

    public static DishModel createSavedDish() {
        return DishModel.builder()
                .id(1L)
                .name("Bandeja Paisa")
                .categoryId(1L)
                .description("Traditional Colombian dish with beans, rice, meat and more")
                .price(25000)
                .restaurantId(1L)
                .urlImage("https://example.com/bandeja-paisa.jpg")
                .active(true)
                .build();
    }
}
