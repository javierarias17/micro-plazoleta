package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.EmployeeLinkRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantSummaryResponseDto;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto);
    void linkEmployee(Long restaurantId, EmployeeLinkRequestDto employeeLinkRequestDto);
    PagedResponseDto<RestaurantSummaryResponseDto> listRestaurants(int page, int pageSize);
}
