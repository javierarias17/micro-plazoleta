package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;

import java.util.List;
import java.util.Optional;

public interface IOrderPersistencePort {
    OrderModel saveOrder(OrderModel orderModel);
    boolean hasActiveOrderByClient(Long clientId, List<OrderStatus> activeStatuses);
    PagedResult<OrderModel> findByRestaurantAndStatus(Long restaurantId, OrderStatus status, int page, int pageSize);
    Optional<OrderModel> findById(Long orderId);
    OrderModel updateOrder(OrderModel orderModel);
}
