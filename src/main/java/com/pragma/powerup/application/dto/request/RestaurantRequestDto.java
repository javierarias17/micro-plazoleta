package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.RegexConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
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
    @NotBlank(message = ValidationMessageConstants.MSG_NAME_REQUIRED)
    @Pattern(regexp = RegexConstants.RESTAURANT_NAME_REGEX, message = ValidationMessageConstants.MSG_NAME_NOT_NUMBERS_ONLY)
    private String name;

    @Schema(description = "Restaurant NIT (digits only)", example = "900123456")
    @NotBlank(message = ValidationMessageConstants.MSG_NIT_REQUIRED)
    @Pattern(regexp = RegexConstants.NIT_REGEX, message = ValidationMessageConstants.MSG_NIT_DIGITS_ONLY)
    private String nit;

    @Schema(description = "Restaurant address", example = "Calle 123 # 45-67")
    @NotBlank(message = ValidationMessageConstants.MSG_ADDRESS_REQUIRED)
    private String address;

    @Schema(description = "Restaurant phone (max 13 chars, optional + prefix)", example = "+573001234567")
    @NotBlank(message = ValidationMessageConstants.MSG_PHONE_REQUIRED)
    @Pattern(regexp = RegexConstants.PHONE_REGEX, message = ValidationMessageConstants.MSG_PHONE_FORMAT)
    private String phone;

    @Schema(description = "Restaurant logo URL", example = "https://la-cosecha-parrillada-colombia.com/logo.png")
    @NotBlank(message = ValidationMessageConstants.MSG_LOGO_URL_REQUIRED)
    private String urlLogo;

    @Schema(description = "ID of the user owner of the restaurant", example = "2")
    @NotNull(message = ValidationMessageConstants.MSG_OWNER_ID_REQUIRED)
    private Long ownerId;
}
