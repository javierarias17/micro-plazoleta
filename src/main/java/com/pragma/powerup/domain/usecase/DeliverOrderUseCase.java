package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IDeliverOrderServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.InvalidSecurityPinException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.OrderNotReadyException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import com.pragma.powerup.domain.validator.FieldValidator;

import com.pragma.powerup.domain.common.DateUtil;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeliverOrderUseCase implements IDeliverOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IUserServicePort userServicePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final ITraceabilityPort traceabilityPort;

    public DeliverOrderUseCase(IOrderPersistencePort orderPersistencePort,
            IUserServicePort userServicePort,
            IAuthenticatedUserPort authenticatedUserPort,
            ITraceabilityPort traceabilityPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.userServicePort = userServicePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.traceabilityPort = traceabilityPort;
    }

    @Override
    public OrderModel deliverOrder(Long orderId, String securityPin) {
        validateFields(orderId, securityPin);

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

        if (order.getStatus() != OrderStatus.LISTO) {
            throw new OrderNotReadyException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.ORDER_ID, FunctionalMessageConstants.ORDER_NOT_READY));
        }

        if (!securityPin.equals(order.getSecurityPin())) {
            throw new InvalidSecurityPinException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.SECURITY_PIN, FunctionalMessageConstants.INVALID_SECURITY_PIN));
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.ENTREGADO);
        OrderModel updatedOrder = orderPersistencePort.updateOrder(order);
        traceabilityPort.logStatusChange(orderId, restaurantId, order.getClientId(), employeeId,
                previousStatus, order.getStatus(), DateUtil.getCurrentDateTime());
        return updatedOrder;
    }

    private void validateFields(Long orderId, String securityPin) {
        Map<String, String> errors = new LinkedHashMap<>();

        FieldValidator.validateNotNull(orderId, FieldConstants.ORDER_ID,
                ValidationMessageConstants.MSG_ORDER_ID_REQUIRED, errors);

        FieldValidator.validateNotBlank(securityPin, FieldConstants.SECURITY_PIN,
                ValidationMessageConstants.MSG_SECURITY_PIN_REQUIRED, errors);

        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }
}
