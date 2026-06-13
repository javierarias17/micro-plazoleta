package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Summary of a restaurant as seen by a customer")
public class RestaurantSummaryResponseDto {

    @Schema(description = "Restaurant ID", example = "1")
    private Long id;

    @Schema(description = "Restaurant name", example = "La Brasa")
    private String name;

    @Schema(description = "Restaurant logo URL", example = "https://example.com/logo.png")
    private String urlLogo;
}
