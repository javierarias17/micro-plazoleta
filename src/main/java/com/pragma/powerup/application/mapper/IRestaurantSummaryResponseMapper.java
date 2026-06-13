package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.response.RestaurantSummaryResponseDto;
import com.pragma.powerup.domain.model.RestaurantModel;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IRestaurantSummaryResponseMapper {
    RestaurantSummaryResponseDto toSummaryResponse(RestaurantModel restaurantModel);
    List<RestaurantSummaryResponseDto> toSummaryResponseList(List<RestaurantModel> restaurantModels);
}
