package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Override
    public OrderModel saveOrder(OrderModel orderModel) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(orderModel);
        orderEntity.getOrderDishes().forEach(dish -> dish.setOrder(orderEntity));
        return orderEntityMapper.toModel(orderRepository.save(orderEntity));
    }

    @Override
    public boolean hasActiveOrderByClient(Long clientId, List<OrderStatus> activeStatuses) {
        return orderRepository.existsByClientIdAndStatusIn(clientId, activeStatuses);
    }

    @Override
    public PagedResult<OrderModel> findByRestaurantAndStatus(Long restaurantId, OrderStatus status,
            int page, int pageSize) {
        Page<OrderEntity> entityPage = orderRepository.findByRestaurant_IdAndStatus(
                restaurantId, status, PageRequest.of(page, pageSize));
        return PagedResult.<OrderModel>builder()
                .content(entityPage.stream()
                        .map(orderEntityMapper::toModel)
                        .collect(Collectors.toList()))
                .totalElements(entityPage.getTotalElements())
                .totalPages(entityPage.getTotalPages())
                .currentPage(entityPage.getNumber())
                .pageSize(entityPage.getSize())
                .build();
    }

    @Override
    public Optional<OrderModel> findById(Long orderId) {
        return orderRepository.findById(orderId).map(orderEntityMapper::toModel);
    }

    @Override
    public OrderModel updateOrder(OrderModel orderModel) {
        OrderEntity orderEntity = orderEntityMapper.toEntity(orderModel);
        orderEntity.getOrderDishes().forEach(dish -> dish.setOrder(orderEntity));
        return orderEntityMapper.toModel(orderRepository.save(orderEntity));
    }
}
