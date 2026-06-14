package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.ValidationMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Schema(description = "Request body to place an order")
public class OrderRequestDto {

    @Schema(description = "Restaurant ID", example = "1")
    @NotNull(message = ValidationMessageConstants.MSG_RESTAURANT_ID_REQUIRED)
    private Long restaurantId;

    @Schema(description = "List of dishes and their quantities")
    @NotEmpty(message = ValidationMessageConstants.MSG_DISHES_REQUIRED)
    @Valid
    private List<OrderDishRequestDto> dishes;
}
