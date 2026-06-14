package com.pragma.powerup.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Schema(description = "Order response")
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private LocalDate date;
    private String status;
    private Long restaurantId;
    private List<OrderDishResponseDto> dishes;
}
