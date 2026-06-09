package com.pragma.powerup.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Schema(description = "Request body to create a restaurant")
public class RestaurantRequestDto {

    @Schema(description = "Restaurant name (cannot be only numbers)", example = "La Cosecha Parrilla")
    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^(?!\\d+$).+$", message = "Name cannot consist of numbers only")
    private String name;

    @Schema(description = "Restaurant NIT (digits only)", example = "900123456")
    @NotBlank(message = "NIT is required")
    @Pattern(regexp = "\\d+", message = "NIT must contain only digits")
    private String nit;

    @Schema(description = "Restaurant address", example = "Calle 123 # 45-67")
    @NotBlank(message = "Address is required")
    private String address;

    @Schema(description = "Restaurant phone (max 13 chars, optional + prefix)", example = "+573001234567")
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(\\+\\d{1,12}|\\d{1,13})$",
            message = "Phone must contain at most 13 characters and must start with + if provided")
    private String phone;

    @Schema(description = "Restaurant logo URL", example = "https://la-cosecha-parrillada-colombia.com/logo.png")
    @NotBlank(message = "Logo URL is required")
    private String urlLogo;

    @Schema(description = "ID of the user owner of the restaurant", example = "2")
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
