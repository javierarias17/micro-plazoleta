package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.application.handler.IOrderHandler;
import com.pragma.powerup.application.mapper.IOrderRequestMapper;
import com.pragma.powerup.application.mapper.IOrderResponseMapper;
import com.pragma.powerup.domain.api.IAssignOrderServicePort;
import com.pragma.powerup.domain.api.ICreateOrderServicePort;
import com.pragma.powerup.domain.api.IListOrdersServicePort;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderHandler implements IOrderHandler {

    private final ICreateOrderServicePort createOrderServicePort;
    private final IListOrdersServicePort listOrdersServicePort;
    private final IAssignOrderServicePort assignOrderServicePort;
    private final IOrderRequestMapper orderRequestMapper;
    private final IOrderResponseMapper orderResponseMapper;

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        return orderResponseMapper.toResponse(
                createOrderServicePort.createOrder(
                        orderRequestMapper.toOrder(orderRequestDto)));
    }

    @Override
    public OrderResponseDto assignOrder(Long orderId) {
        return orderResponseMapper.toResponse(assignOrderServicePort.assignOrder(orderId));
    }

    @Override
    public PagedResponseDto<OrderResponseDto> listOrders(OrderStatus status, int page, int pageSize) {
        PagedResult<OrderModel> pagedResult = listOrdersServicePort.listOrders(status, page, pageSize);
        PagedResponseDto<OrderResponseDto> response = new PagedResponseDto<>();
        response.setContent(orderResponseMapper.toResponseList(pagedResult.getContent()));
        response.setTotalElements(pagedResult.getTotalElements());
        response.setTotalPages(pagedResult.getTotalPages());
        response.setCurrentPage(pagedResult.getCurrentPage());
        response.setPageSize(pagedResult.getPageSize());
        return response;
    }
}
