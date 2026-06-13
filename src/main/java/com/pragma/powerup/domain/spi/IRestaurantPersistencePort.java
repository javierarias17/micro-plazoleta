package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.Optional;

public interface IRestaurantPersistencePort {
    RestaurantModel saveRestaurant(RestaurantModel restaurantModel);
    Optional<RestaurantModel> findRestaurantById(Long id);
    PagedResult<RestaurantModel> findAllSortedByName(int page, int pageSize);
}
