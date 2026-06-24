package com.pragma.powerup.infrastructure.out.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderLogRequest {

    private Long orderId;
    private Long restaurantId;
    private Long customerId;
    private Long employeeId;
    private String previousStatus;
    private String newStatus;
    private LocalDateTime date;
}
