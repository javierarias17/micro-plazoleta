package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.ValidationMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Schema(description = "Request body to enable or disable a dish")
public class DishStatusRequestDto {

    @Schema(description = "New active status for the dish", example = "false")
    @NotNull(message = ValidationMessageConstants.MSG_ACTIVE_REQUIRED)
    private Boolean active;
}
