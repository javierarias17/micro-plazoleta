package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantSummaryResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.application.mapper.IRestaurantSummaryResponseMapper;
import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.api.IListRestaurantsServicePort;
import com.pragma.powerup.domain.api.IValidateRestaurantOwnerServicePort;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final ICreateRestaurantServicePort createRestaurantServicePort;
    private final IValidateRestaurantOwnerServicePort validateRestaurantOwnerServicePort;
    private final IListRestaurantsServicePort listRestaurantsServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IRestaurantSummaryResponseMapper restaurantSummaryResponseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        return restaurantResponseMapper.toResponse(
                createRestaurantServicePort.createRestaurant(
                        restaurantRequestMapper.toRestaurant(restaurantRequestDto)));
    }

    @Override
    public boolean isOwner(Long restaurantId) {
        return validateRestaurantOwnerServicePort.isOwner(restaurantId);
    }

    @Override
    public PagedResponseDto<RestaurantSummaryResponseDto> listRestaurants(int page, int pageSize) {
        PagedResult<RestaurantModel> pagedResult = listRestaurantsServicePort.listRestaurants(page, pageSize);
        PagedResponseDto<RestaurantSummaryResponseDto> response = new PagedResponseDto<>();
        response.setContent(restaurantSummaryResponseMapper.toSummaryResponseList(pagedResult.getContent()));
        response.setTotalElements(pagedResult.getTotalElements());
        response.setTotalPages(pagedResult.getTotalPages());
        response.setCurrentPage(pagedResult.getCurrentPage());
        response.setPageSize(pagedResult.getPageSize());
        return response;
    }
}
