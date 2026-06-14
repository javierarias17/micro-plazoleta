package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.domain.model.OrderStatus;

public interface IOrderHandler {
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);
    PagedResponseDto<OrderResponseDto> listOrders(OrderStatus status, int page, int pageSize);
}
