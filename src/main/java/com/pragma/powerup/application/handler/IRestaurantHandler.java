package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.EmployeeLinkRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;

public interface IRestaurantHandler {
    RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto);
    void linkEmployee(Long restaurantId, EmployeeLinkRequestDto employeeLinkRequestDto);
}
