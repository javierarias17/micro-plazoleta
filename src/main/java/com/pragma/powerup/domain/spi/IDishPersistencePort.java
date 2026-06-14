package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PagedResult;

import java.util.List;
import java.util.Optional;

public interface IDishPersistencePort {
    DishModel saveDish(DishModel dishModel);
    Optional<DishModel> findDishById(Long id);
    DishModel updateDish(DishModel dishModel);
    PagedResult<DishModel> findDishesByRestaurant(Long restaurantId, Long categoryId, int page, int pageSize);
    List<Long> findActiveDishesByIdsAndRestaurant(List<Long> ids, Long restaurantId);
}
