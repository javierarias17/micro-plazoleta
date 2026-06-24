package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICreateOrderServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
import com.pragma.powerup.domain.exception.CustomerHasActiveOrderException;
import com.pragma.powerup.domain.exception.DishNotFromRestaurantException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.validator.FieldValidator;

import com.pragma.powerup.domain.common.DateUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreateOrderUseCase implements ICreateOrderServicePort {

    private static final List<OrderStatus> ACTIVE_STATUSES =
            List.of(OrderStatus.PENDIENTE, OrderStatus.EN_PREPARACION, OrderStatus.LISTO);

    private final IOrderPersistencePort orderPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IDishPersistencePort dishPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;
    private final ITraceabilityPort traceabilityPort;

    public CreateOrderUseCase(IOrderPersistencePort orderPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IDishPersistencePort dishPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort,
            ITraceabilityPort traceabilityPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.dishPersistencePort = dishPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
        this.traceabilityPort = traceabilityPort;
    }

    @Override
    public OrderModel createOrder(OrderModel orderModel) {
        this.validateFields(orderModel);

        Long clientId = authenticatedUserPort.getAuthenticatedUserId();
        this.validateBusinessRules(orderModel, clientId);
        
        orderModel.setClientId(clientId);
        orderModel.setDate(DateUtil.getCurrentDate());
        orderModel.setStatus(OrderStatus.PENDIENTE);

        OrderModel savedOrder = orderPersistencePort.saveOrder(orderModel);
        traceabilityPort.saveOrderLog(savedOrder.getId(), savedOrder.getRestaurantId(), clientId, null, null, savedOrder.getStatus(), DateUtil.getCurrentDateTime());
        return savedOrder;
    }

    private void validateFields(OrderModel orderModel) {
        Map<String, String> errors = new LinkedHashMap<>();

        FieldValidator.validateNotNull(orderModel.getRestaurantId(), FieldConstants.RESTAURANT_ID,
                ValidationMessageConstants.MSG_RESTAURANT_ID_REQUIRED, errors);

        if (orderModel.getDishes() == null || orderModel.getDishes().isEmpty()) {
            errors.put(FieldConstants.DISHES, ValidationMessageConstants.MSG_DISHES_REQUIRED);
        } else {
            for (OrderDishModel dish : orderModel.getDishes()) {
                FieldValidator.validateNotNull(dish.getDishId(), FieldConstants.DISH_ID,
                        ValidationMessageConstants.MSG_DISH_ID_REQUIRED, errors);
                FieldValidator.validateNotNull(dish.getQuantity(), FieldConstants.QUANTITY,
                        ValidationMessageConstants.MSG_QUANTITY_REQUIRED, errors);
                FieldValidator.validatePositive(dish.getQuantity(), FieldConstants.QUANTITY,
                        ValidationMessageConstants.MSG_QUANTITY_POSITIVE, errors);
            }
        }

        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }

    private void validateBusinessRules(OrderModel orderModel, Long clientId) {

        if (orderPersistencePort.hasActiveOrderByClient(clientId, ACTIVE_STATUSES)) {
            throw new CustomerHasActiveOrderException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.CLIENT_ID,
                            FunctionalMessageConstants.CUSTOMER_HAS_ACTIVE_ORDER));
        }

        restaurantPersistencePort.findRestaurantById(orderModel.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.RESTAURANT_ID,
                                FunctionalMessageConstants.RESTAURANT_NOT_FOUND)));

        List<Long> requestedDishIds = orderModel.getDishes().stream()
                .map(OrderDishModel::getDishId)
                .toList();

        List<Long> foundDishIds = dishPersistencePort
                .findActiveDishesByIdsAndRestaurant(requestedDishIds, orderModel.getRestaurantId());

        if (foundDishIds.size() != requestedDishIds.size()) {
            throw new DishNotFromRestaurantException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.DISHES,
                            FunctionalMessageConstants.DISH_NOT_AVAILABLE));
        }
    }
}
