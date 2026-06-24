package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderStatus;

import java.time.LocalDateTime;

public interface ITraceabilityPort {
    void saveOrderLog(Long orderId, Long restaurantId, Long customerId, Long employeeId,
                      OrderStatus previousStatus, OrderStatus newStatus,
                      LocalDateTime occurredAt);
}
