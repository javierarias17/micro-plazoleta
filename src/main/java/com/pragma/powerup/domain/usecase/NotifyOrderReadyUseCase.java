package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.INotifyOrderReadyServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.INotifyClientPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.domain.spi.IUserServicePort;

import java.security.SecureRandom;
import com.pragma.powerup.domain.common.DateUtil;

import java.util.Map;

public class NotifyOrderReadyUseCase implements INotifyOrderReadyServicePort {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final IOrderPersistencePort orderPersistencePort;
    private final IUserServicePort userServicePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final INotifyClientPort notifyClientPort;
    private final ITraceabilityPort traceabilityPort;

    public NotifyOrderReadyUseCase(IOrderPersistencePort orderPersistencePort,
            IUserServicePort userServicePort,
            IAuthenticatedUserPort authenticatedUserPort,
            INotifyClientPort notifyClientPort,
            ITraceabilityPort traceabilityPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.userServicePort = userServicePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.notifyClientPort = notifyClientPort;
        this.traceabilityPort = traceabilityPort;
    }

    @Override
    public OrderModel notifyOrderReady(Long orderId) {
        Long employeeId = authenticatedUserPort.getAuthenticatedUserId();

        Long restaurantId = userServicePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new RestaurantNotFoundException(FunctionalMessageConstants.RESTAURANT_NOT_FOUND,
                        Map.of()));

        OrderModel order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.ORDER_ID, FunctionalMessageConstants.ORDER_NOT_FOUND)));

        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new ForbiddenException();
        }

        if (!employeeId.equals(order.getChefId())) {
            throw new ForbiddenException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.ORDER_ID, FunctionalMessageConstants.ORDER_ASSIGNED_TO_ANOTHER_EMPLOYEE));
        }

        if (order.getStatus() != OrderStatus.EN_PREPARACION) {
            throw new OrderNotInPreparationException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.ORDER_ID, FunctionalMessageConstants.ORDER_NOT_IN_PREPARATION));
        }

        order.setSecurityPin(generatePin());
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.LISTO);
        OrderModel savedOrder = orderPersistencePort.updateOrder(order);

        notifyClientPort.notify(order.getClientId(), order.getSecurityPin());
        traceabilityPort.logStatusChange(orderId, restaurantId, order.getClientId(), employeeId,
                previousStatus, order.getStatus(), DateUtil.getCurrentDateTime());

        return savedOrder;
    }

    private String generatePin() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }
}
