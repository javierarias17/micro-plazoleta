package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;

public interface IListOrdersServicePort {
    PagedResult<OrderModel> listOrders(OrderStatus status, int page, int pageSize);
}
