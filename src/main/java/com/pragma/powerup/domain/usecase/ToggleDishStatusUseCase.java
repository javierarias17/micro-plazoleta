package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IToggleDishStatusServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.validator.FieldValidator;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToggleDishStatusUseCase implements IToggleDishStatusServicePort {

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public ToggleDishStatusUseCase(IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public DishModel toggleDishStatus(Long id, Boolean active) {
        this.validateForToggleDishStatus(id, active);

        DishModel dish = dishPersistencePort.findDishById(id)
                .orElseThrow(() -> new DishNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.DISH_ID, FunctionalMessageConstants.DISH_NOT_FOUND)));

        validateOwnership(dish);

        dish.setActive(active);
        return dishPersistencePort.updateDish(dish);
    }

    private void validateForToggleDishStatus(Long id, Boolean active) {
        Map<String, String> errors = new LinkedHashMap<>();

        FieldValidator.validateNotNull(id, FieldConstants.DISH_ID,
                ValidationMessageConstants.MSG_DISH_ID_REQUIRED, errors);

        FieldValidator.validateNotNull(active, FieldConstants.ACTIVE,
                ValidationMessageConstants.MSG_ACTIVE_REQUIRED, errors);

        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }

    private void validateOwnership(DishModel dish) {
        RestaurantModel restaurant = restaurantPersistencePort
                .findRestaurantById(dish.getRestaurantId())
                .orElse(null);

        if (restaurant != null && !restaurant.getOwnerId().equals(authenticatedUserPort.getAuthenticatedUserId())) {
            throw new ForbiddenException();
        }
    }
}
