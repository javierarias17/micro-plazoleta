package com.pragma.powerup.factory;

import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.List;

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

    public static PagedResult<RestaurantModel> createRestaurantPagedResult() {
        List<RestaurantModel> content = List.of(
                RestaurantModel.builder().id(1L).name("Asados del Valle").urlLogo("https://example.com/asados.png").build(),
                RestaurantModel.builder().id(2L).name("La Brasa").urlLogo("https://example.com/brasa.png").build(),
                RestaurantModel.builder().id(3L).name("Zafiro Cocina").urlLogo("https://example.com/zafiro.png").build()
        );
        return PagedResult.<RestaurantModel>builder()
                .content(content)
                .totalElements(9L)
                .totalPages(3)
                .currentPage(0)
                .pageSize(3)
                .build();
    }
}
