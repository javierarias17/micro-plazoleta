package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.DishModel;

import java.util.Optional;

public interface IDishPersistencePort {
    DishModel saveDish(DishModel dishModel);
    Optional<DishModel> findDishById(Long id);
    DishModel updateDish(DishModel dishModel);
}
