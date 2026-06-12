package com.pragma.powerup.application.dto.request;

import com.pragma.powerup.domain.common.ValidationMessageConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Schema(description = "Request body to link an employee to a restaurant")
public class EmployeeLinkRequestDto {

    @Schema(description = "ID of the employee user to link", example = "5")
    @NotNull(message = ValidationMessageConstants.MSG_EMPLOYEE_ID_REQUIRED)
    private Long employeeId;
}
