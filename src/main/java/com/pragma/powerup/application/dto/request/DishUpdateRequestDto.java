package com.pragma.powerup.application.dto.request;

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
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Integer price;

    @Schema(description = "Dish description", example = "Updated traditional Colombian dish")
    @NotBlank(message = "Description is required")
    private String description;
}
