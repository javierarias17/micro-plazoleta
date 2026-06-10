package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUpdateDishServicePort;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.DomainExceptionConstants;
import com.pragma.powerup.domain.exception.FunctionalExceptionResponse;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.spi.IDishPersistencePort;

import java.util.Map;

public class UpdateDishUseCase implements IUpdateDishServicePort {

    private final IDishPersistencePort dishPersistencePort;

    public UpdateDishUseCase(IDishPersistencePort dishPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
    }

    @Override
    public DishModel updateDish(Long id, Integer price, String description) {
        DishModel dish = dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(
                        FunctionalExceptionResponse.BUSINESS_VALIDATION_FAILED.getMessage(),
                        Map.of(DomainExceptionConstants.DISH_ID,
                                FunctionalExceptionResponse.DISH_NOT_FOUND.getMessage())));

        dish.setPrice(price);
        dish.setDescription(description);

        // TODO HU-05: validar que el propietario autenticado (obtenido de los claims del JWT)
        // sea el dueño del restaurante al que pertenece el plato antes de modificarlo.

        return dishPersistencePort.updateDish(dish);
    }
}
