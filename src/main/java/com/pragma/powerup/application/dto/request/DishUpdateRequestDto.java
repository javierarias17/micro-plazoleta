package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.ValidationMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Schema(description = "Request body to update a dish (only price and description allowed)")
public class DishUpdateRequestDto {

    @Schema(description = "Dish price (must be positive)", example = "28000")
    @NotNull(message = ValidationMessageConstants.MSG_PRICE_REQUIRED)
    @Positive(message = ValidationMessageConstants.MSG_PRICE_POSITIVE)
    private Integer price;

    @Schema(description = "Dish description", example = "Updated traditional Colombian dish")
    @NotBlank(message = ValidationMessageConstants.MSG_DESCRIPTION_REQUIRED)
    private String description;
}
