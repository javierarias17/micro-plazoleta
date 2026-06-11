package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICreateDishServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.*;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.util.Map;

public class CreateDishUseCase implements ICreateDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public CreateDishUseCase(IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            ICategoryPersistencePort categoryPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.categoryPersistencePort = categoryPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public DishModel createDish(DishModel dishModel) {
        this.validateData(dishModel);
        dishModel.setActive(true);
        return dishPersistencePort.saveDish(dishModel);
    }

    private void validateData(DishModel dishModel) {
        RestaurantModel restaurant = restaurantPersistencePort
                .findRestaurantById(dishModel.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.RESTAURANT_ID,
                                FunctionalMessageConstants.RESTAURANT_NOT_FOUND)));

        categoryPersistencePort
                .findCategoryById(dishModel.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.CATEGORY_ID,
                                FunctionalMessageConstants.CATEGORY_NOT_FOUND)));

        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        if (!restaurant.getOwnerId().equals(authenticatedUserId)) {
            throw new OwnerNotAuthorizedException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.OWNER_ID,
                            FunctionalMessageConstants.OWNER_NOT_AUTHORIZED_TO_CREATE_DISH));
        }
    }
}
