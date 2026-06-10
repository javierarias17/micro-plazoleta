package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Schema(description = "Request body to create a dish")
public class DishRequestDto {

    @Schema(description = "Dish name", example = "Bandeja Paisa")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Category ID", example = "1")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = "Dish description", example = "Traditional Colombian dish with beans, rice, meat and more")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Dish price (must be positive)", example = "25000")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be a positive number")
    private Integer price;

    @Schema(description = "Restaurant ID", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Schema(description = "Dish image URL", example = "https://example.com/bandeja-paisa.jpg")
    @NotBlank(message = "Image URL is required")
    private String urlImage;
}
