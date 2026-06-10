package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response body after creating a dish")
public class DishResponseDto {

    @Schema(description = "Generated dish ID", example = "1")
    private Long id;

    @Schema(description = "Dish name", example = "Bandeja Paisa")
    private String name;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Dish description", example = "Traditional Colombian dish with beans, rice, meat and more")
    private String description;

    @Schema(description = "Dish price", example = "25000")
    private Integer price;

    @Schema(description = "Restaurant ID", example = "1")
    private Long restaurantId;

    @Schema(description = "Dish image URL", example = "https://example.com/bandeja-paisa.jpg")
    private String urlImage;

    @Schema(description = "Whether the dish is active", example = "true")
    private Boolean active;
}
