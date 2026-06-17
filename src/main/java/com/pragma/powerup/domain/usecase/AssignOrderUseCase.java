package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IAssignOrderServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;

import java.util.Map;

public class AssignOrderUseCase implements IAssignOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public AssignOrderUseCase(IOrderPersistencePort orderPersistencePort,
            IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
            IAuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public OrderModel assignOrder(Long orderId) {
        Long employeeId = authenticatedUserPort.getAuthenticatedUserId();

        Long restaurantId = restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(employeeId)
                .orElseThrow(() -> new RestaurantNotFoundException(FunctionalMessageConstants.RESTAURANT_NOT_FOUND,
                        Map.of()));

        OrderModel order = orderPersistencePort.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.ORDER_ID,
                                FunctionalMessageConstants.ORDER_NOT_FOUND)));

        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new ForbiddenException();
        }

        if (order.getStatus() != OrderStatus.PENDIENTE) {
            throw new OrderNotAssignableException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.ORDER_ID,
                            FunctionalMessageConstants.ORDER_NOT_IN_ASSIGNABLE_STATUS));
        }

        order.setChefId(employeeId);
        order.setStatus(OrderStatus.EN_PREPARACION);

        return orderPersistencePort.updateOrder(order);
    }
}
