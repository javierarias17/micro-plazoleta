package com.pragma.powerup.factory;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PagedResult;

import java.util.List;

public class DishModelFactory {

    private DishModelFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static PagedResult<DishModel> createDishPagedResult() {
        List<DishModel> content = List.of(
                DishModel.builder().id(1L).name("Ajiaco").categoryId(1L).description("Sopa tradicional").price(18000).restaurantId(1L).urlImage("https://example.com/ajiaco.jpg").active(true).build(),
                DishModel.builder().id(2L).name("Bandeja Paisa").categoryId(1L).description("Plato típico").price(25000).restaurantId(1L).urlImage("https://example.com/bandeja.jpg").active(true).build(),
                DishModel.builder().id(3L).name("Sancocho").categoryId(2L).description("Caldo de pollo").price(15000).restaurantId(1L).urlImage("https://example.com/sancocho.jpg").active(true).build()
        );
        return PagedResult.<DishModel>builder()
                .content(content)
                .totalElements(6L)
                .totalPages(2)
                .currentPage(0)
                .pageSize(3)
                .build();
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
