package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishEntityMapper {

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "restaurant.id", source = "restaurantId")
    DishEntity toEntity(DishModel dishModel);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    DishModel toDishModel(DishEntity dishEntity);
}
