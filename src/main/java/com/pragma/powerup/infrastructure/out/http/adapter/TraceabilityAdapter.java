package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.constant.TechnicalMessageConstants;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.infrastructure.out.http.client.ITraceabilityFeignClient;
import com.pragma.powerup.infrastructure.out.http.dto.OrderLogRequest;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class TraceabilityAdapter implements ITraceabilityPort {

    private final ITraceabilityFeignClient feignClient;

    @Override
    public void saveOrderLog(Long orderId, Long restaurantId, Long customerId, Long employeeId,
                             OrderStatus previousStatus, OrderStatus newStatus,
                             LocalDateTime occurredAt) {
        try {
            feignClient.saveOrderLog(new OrderLogRequest(
                    orderId, restaurantId, customerId, employeeId,
                    previousStatus != null ? previousStatus.name() : null,
                    newStatus.name(),
                    occurredAt
            ));
        } catch (RetryableException e) {
            throw new TechnicalException(TechnicalMessageConstants.TRACEABILITY_UNAVAILABLE);
        } catch (FeignException e) {
            throw new TechnicalException(e.getMessage());
        }
    }
}
