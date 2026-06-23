package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICancelOrderServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.OrderNotCancellableException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;

import com.pragma.powerup.domain.common.DateUtil;

import java.util.Map;

public class CancelOrderUseCase implements ICancelOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final ITraceabilityPort traceabilityPort;

    public CancelOrderUseCase(IOrderPersistencePort orderPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort,
            ITraceabilityPort traceabilityPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.traceabilityPort = traceabilityPort;
    }

    @Override
    public OrderModel cancelOrder(Long orderId) {
        Long customerId = authenticatedUserPort.getAuthenticatedUserId();

        OrderModel order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.ORDER_ID, FunctionalMessageConstants.ORDER_NOT_FOUND)));

        this.validateOwnership(order, customerId);
        this.validateCancellableStatus(order);

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.CANCELADO);
        OrderModel updatedOrder = orderPersistencePort.updateOrder(order);
        traceabilityPort.logStatusChange(orderId, order.getRestaurantId(), order.getClientId(), null,
                previousStatus, order.getStatus(), DateUtil.getCurrentDateTime());
        return updatedOrder;
    }

    private void validateOwnership(OrderModel order, Long customerId) {
        if (!order.getClientId().equals(customerId)) {
            throw new ForbiddenException();
        }
    }

    private void validateCancellableStatus(OrderModel order) {
        if (order.getStatus() == OrderStatus.CANCELADO) {
            throw new OrderNotCancellableException(
                    FunctionalMessageConstants.ORDER_ALREADY_CANCELLED,
                    Map.of());
        }

        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new OrderNotCancellableException(
                    FunctionalMessageConstants.ORDER_NOT_CANCELLABLE,
                    Map.of());
        }
    }
}
