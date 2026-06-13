package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PagedResult;

public interface IListDishesServicePort {
    PagedResult<DishModel> listDishes(Long restaurantId, Long categoryId, int page, int pageSize);
}
