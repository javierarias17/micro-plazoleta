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
@Schema(description = "Request body to create a dish")
public class DishRequestDto {

    @Schema(description = "Dish name", example = "Bandeja Paisa")
    @NotBlank(message = ValidationMessageConstants.MSG_NAME_REQUIRED)
    private String name;

    @Schema(description = "Category ID", example = "1")
    @NotNull(message = ValidationMessageConstants.MSG_CATEGORY_ID_REQUIRED)
    private Long categoryId;

    @Schema(description = "Dish description", example = "Traditional Colombian dish with beans, rice, meat and more")
    @NotBlank(message = ValidationMessageConstants.MSG_DESCRIPTION_REQUIRED)
    private String description;

    @Schema(description = "Dish price (must be positive)", example = "25000")
    @NotNull(message = ValidationMessageConstants.MSG_PRICE_REQUIRED)
    @Positive(message = ValidationMessageConstants.MSG_PRICE_POSITIVE)
    private Integer price;

    @Schema(description = "Restaurant ID", example = "1")
    @NotNull(message = ValidationMessageConstants.MSG_RESTAURANT_ID_REQUIRED)
    private Long restaurantId;

    @Schema(description = "Dish image URL", example = "https://example.com/bandeja-paisa.jpg")
    @NotBlank(message = ValidationMessageConstants.MSG_IMAGE_URL_REQUIRED)
    private String urlImage;
}
