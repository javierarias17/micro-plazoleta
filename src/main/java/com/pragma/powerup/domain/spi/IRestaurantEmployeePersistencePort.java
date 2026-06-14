package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;

import java.util.Optional;

public interface IRestaurantEmployeePersistencePort {
    boolean existsByEmployeeId(Long employeeId);
    RestaurantEmployeeModel save(RestaurantEmployeeModel restaurantEmployeeModel);
    Optional<Long> findRestaurantIdByEmployeeId(Long employeeId);
}
