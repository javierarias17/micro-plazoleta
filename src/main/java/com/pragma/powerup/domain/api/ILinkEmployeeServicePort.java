package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;

public interface ILinkEmployeeServicePort {
    RestaurantEmployeeModel linkEmployee(Long restaurantId, Long employeeId);
}
