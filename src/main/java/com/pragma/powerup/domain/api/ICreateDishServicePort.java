package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DishModel;

public interface ICreateDishServicePort {
    DishModel createDish(DishModel dishModel);
}
