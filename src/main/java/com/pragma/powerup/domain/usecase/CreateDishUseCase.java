package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICreateDishServicePort;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.util.Map;

public class CreateDishUseCase implements ICreateDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    public CreateDishUseCase(IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            ICategoryPersistencePort categoryPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
    }

    @Override
    public DishModel createDish(DishModel dishModel) {
        this.validateData(dishModel);
        dishModel.setActive(true);
        return dishPersistencePort.saveDish(dishModel);
    }

    private void validateData(DishModel dishModel) {
        restaurantPersistencePort
                .findRestaurantById(dishModel.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                        FunctionalExceptionResponse.BUSINESS_VALIDATION_FAILED.getMessage(),
                        Map.of(DomainExceptionConstants.RESTAURANT_ID,
                                FunctionalExceptionResponse.RESTAURANT_NOT_FOUND.getMessage())));

        categoryPersistencePort
                .findCategoryById(dishModel.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        FunctionalExceptionResponse.BUSINESS_VALIDATION_FAILED.getMessage(),
                        Map.of(DomainExceptionConstants.CATEGORY_ID,
                                FunctionalExceptionResponse.CATEGORY_NOT_FOUND.getMessage())));

        // TODO HU-05: validar que el propietario autenticado (obtenido de los claims
        // del JWT)
        // sea el dueño del restaurante antes de crear el plato.
        // if (...) {
        // throw new OwnerNotAuthorizedException(...);
        // }
    }
}
