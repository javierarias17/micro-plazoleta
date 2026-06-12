package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.DishModel;

public interface IToggleDishStatusServicePort {
    DishModel toggleDishStatus(Long id, Boolean active);
}
