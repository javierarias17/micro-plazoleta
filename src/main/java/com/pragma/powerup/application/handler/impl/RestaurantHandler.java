package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.EmployeeLinkRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.api.ILinkEmployeeServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final ICreateRestaurantServicePort createRestaurantServicePort;
    private final ILinkEmployeeServicePort linkEmployeeServicePort;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

    @Override
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto restaurantRequestDto) {
        return restaurantResponseMapper.toResponse(
                createRestaurantServicePort.createRestaurant(
                        restaurantRequestMapper.toRestaurant(restaurantRequestDto)));
    }

    @Override
    public void linkEmployee(Long restaurantId, EmployeeLinkRequestDto employeeLinkRequestDto) {
        linkEmployeeServicePort.linkEmployee(restaurantId, employeeLinkRequestDto.getEmployeeId());
    }
}
