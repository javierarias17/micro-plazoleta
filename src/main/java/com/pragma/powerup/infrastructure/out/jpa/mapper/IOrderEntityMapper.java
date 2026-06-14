package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {IOrderDishEntityMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderEntityMapper {

    @Mapping(target = "restaurant.id", source = "restaurantId")
    @Mapping(target = "orderDishes", source = "dishes")
    OrderEntity toEntity(OrderModel orderModel);

    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "dishes", source = "orderDishes")
    OrderModel toModel(OrderEntity orderEntity);
}
