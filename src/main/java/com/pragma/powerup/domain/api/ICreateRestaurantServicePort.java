package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantModel;

public interface ICreateRestaurantServicePort {
    RestaurantModel createRestaurant(RestaurantModel restaurantModel);
}
