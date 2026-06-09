package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Response body after creating a restaurant")
public class RestaurantResponseDto {

    @Schema(description = "Generated restaurant ID", example = "1")
    private Long id;

    @Schema(description = "Restaurant name", example = "La Cosecha Parrillada")
    private String name;

    @Schema(description = "Restaurant NIT", example = "900123456")
    private String nit;

    @Schema(description = "Restaurant address", example = "Calle 123 # 45-67")
    private String address;

    @Schema(description = "Restaurant phone", example = "+573001234567")
    private String phone;

    @Schema(description = "Restaurant logo URL", example = "https://la-cosecha-parrillada-colombia.com/logo.png")
    private String urlLogo;

    @Schema(description = "ID of the owner user", example = "2")
    private Long ownerId;
}
