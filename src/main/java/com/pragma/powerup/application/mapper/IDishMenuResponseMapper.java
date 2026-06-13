package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.DishMenuResponseDto;
import com.pragma.powerup.domain.model.DishModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IDishMenuResponseMapper {
    DishMenuResponseDto toMenuResponse(DishModel dishModel);
    List<DishMenuResponseDto> toMenuResponseList(List<DishModel> dishModels);
}
