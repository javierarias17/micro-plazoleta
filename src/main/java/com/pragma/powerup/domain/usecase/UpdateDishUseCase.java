package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IUpdateDishServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.OwnerNotAuthorizedException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.validator.FieldValidator;

import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateDishUseCase implements IUpdateDishServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public UpdateDishUseCase(IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public DishModel updateDish(Long id, Integer price, String description) {
        this.validateFields(price, description);

        DishModel dish = dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.DISH_ID,
                                FunctionalMessageConstants.DISH_NOT_FOUND)));

        this.validateBusinessRules(dish);

        dish.setPrice(price);
        dish.setDescription(description);

        return dishPersistencePort.updateDish(dish);
    }

    private void validateFields(Integer price, String description) {
        Map<String, String> errors = new LinkedHashMap<>();

        FieldValidator.validateNotNull(price, FieldConstants.PRICE,
                ValidationMessageConstants.MSG_PRICE_REQUIRED, errors);
        FieldValidator.validatePositive(price, FieldConstants.PRICE,
                ValidationMessageConstants.MSG_PRICE_POSITIVE, errors);

        FieldValidator.validateNotBlank(description, FieldConstants.DESCRIPTION,
                ValidationMessageConstants.MSG_DESCRIPTION_REQUIRED, errors);

        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }

    private void validateBusinessRules(DishModel dish) {
        RestaurantModel restaurant = restaurantPersistencePort
                .findRestaurantById(dish.getRestaurantId())
                .orElseThrow(() -> new DishNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.DISH_ID,
                                FunctionalMessageConstants.DISH_NOT_FOUND)));

        Long authenticatedUserId = authenticatedUserPort.getAuthenticatedUserId();
        if (!restaurant.getOwnerId().equals(authenticatedUserId)) {
            throw new OwnerNotAuthorizedException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.OWNER_ID,
                            FunctionalMessageConstants.OWNER_NOT_AUTHORIZED_TO_UPDATE_DISH));
        }
    }
}
