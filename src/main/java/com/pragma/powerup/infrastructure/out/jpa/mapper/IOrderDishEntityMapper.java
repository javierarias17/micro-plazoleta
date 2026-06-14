package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.OrderDishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IOrderDishEntityMapper {

    @Mapping(target = "dish.id", source = "dishId")
    OrderDishEntity toEntity(OrderDishModel orderDishModel);

    @Mapping(target = "dishId", source = "dish.id")
    OrderDishModel toModel(OrderDishEntity orderDishEntity);

    List<OrderDishModel> toModelList(List<OrderDishEntity> orderDishEntities);
}
