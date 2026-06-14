package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.ValidationMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Schema(description = "Dish and quantity within an order")
public class OrderDishRequestDto {

    @Schema(description = "Dish ID", example = "1")
    @NotNull(message = ValidationMessageConstants.MSG_DISH_ID_REQUIRED)
    private Long dishId;

    @Schema(description = "Number of portions of the dish", example = "2")
    @NotNull(message = ValidationMessageConstants.MSG_QUANTITY_REQUIRED)
    @Positive(message = ValidationMessageConstants.MSG_QUANTITY_POSITIVE)
    private Integer quantity;
}
