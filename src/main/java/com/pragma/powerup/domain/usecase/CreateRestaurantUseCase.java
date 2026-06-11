package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.exception.OwnerNotFoundException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;

import java.util.Map;

public class CreateRestaurantUseCase implements ICreateRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPort userValidationPort;

    public CreateRestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                                   IUserValidationPort userValidationPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userValidationPort = userValidationPort;
    }

    @Override
    public RestaurantModel createRestaurant(RestaurantModel restaurantModel) {
        validateData(restaurantModel);
        return restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    private void validateData(RestaurantModel restaurantModel) {
        if (!userValidationPort.isOwner(restaurantModel.getOwnerId())) {
            throw new OwnerNotFoundException(FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.OWNER_ID, FunctionalMessageConstants.OWNER_NOT_FOUND));
        }
    }
}
