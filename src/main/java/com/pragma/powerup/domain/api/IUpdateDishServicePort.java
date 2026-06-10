package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DishModel;

public interface IUpdateDishServicePort {
    DishModel updateDish(Long id, Integer price, String description);
}
