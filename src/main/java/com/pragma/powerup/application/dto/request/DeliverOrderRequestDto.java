package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.ValidationMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Schema(description = "Request body to deliver an order")
public class DeliverOrderRequestDto {

    @Schema(description = "Security PIN sent to the customer when the order was marked as ready", example = "482951")
    @NotBlank(message = ValidationMessageConstants.MSG_SECURITY_PIN_REQUIRED)
    private String securityPin;
}
