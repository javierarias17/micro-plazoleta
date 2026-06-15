package com.pragma.powerup.factory;

import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class OrderModelFactory {

    private OrderModelFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static OrderModel createValidOrderRequest() {
        return OrderModel.builder()
                .restaurantId(1L)
                .dishes(List.of(
                        OrderDishModel.builder().dishId(1L).quantity(2).build(),
                        OrderDishModel.builder().dishId(2L).quantity(1).build()
                ))
                .build();
    }

    public static OrderModel createSavedOrder() {
        return OrderModel.builder()
                .id(1L)
                .clientId(10L)
                .date(LocalDate.of(2024, Month.JANUARY, 1))
                .status(OrderStatus.PENDIENTE)
                .restaurantId(1L)
                .dishes(List.of(
                        OrderDishModel.builder().dishId(1L).quantity(2).build(),
                        OrderDishModel.builder().dishId(2L).quantity(1).build()
                ))
                .build();
    }

    public static List<Long> createActiveDishIdsForRestaurant() {
        return List.of(1L, 2L);
    }

    public static PagedResult<OrderModel> createOrderPagedResult() {
        List<OrderModel> orders = List.of(
                OrderModel.builder().id(1L).clientId(10L).date(LocalDate.of(2024, Month.JANUARY, 1))
                        .status(OrderStatus.PENDIENTE).restaurantId(1L).build(),
                OrderModel.builder().id(2L).clientId(11L).date(LocalDate.of(2024, Month.JANUARY, 1))
                        .status(OrderStatus.PENDIENTE).restaurantId(1L).build()
        );
        return PagedResult.<OrderModel>builder()
                .content(orders)
                .totalElements(2L)
                .totalPages(1)
                .currentPage(0)
                .pageSize(10)
                .build();
    }
}
