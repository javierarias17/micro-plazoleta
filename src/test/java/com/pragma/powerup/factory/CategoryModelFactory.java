package com.pragma.powerup.factory;

import com.pragma.powerup.domain.model.CategoryModel;

public class CategoryModelFactory {

    private CategoryModelFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static CategoryModel createSavedCategory() {
        return CategoryModel.builder()
                .id(1L)
                .name("Carnes y Asados")
                .description("Cortes de carne, parrillas y asados")
                .build();
    }
}
