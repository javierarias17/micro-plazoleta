package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dish and quantity in an order response")
public class OrderDishResponseDto {
    private Long dishId;
    private Integer quantity;
}
