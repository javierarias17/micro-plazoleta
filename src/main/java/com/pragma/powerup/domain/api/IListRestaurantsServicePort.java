package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.RestaurantModel;

public interface IListRestaurantsServicePort {
    PagedResult<RestaurantModel> listRestaurants(int page, int pageSize);
}
