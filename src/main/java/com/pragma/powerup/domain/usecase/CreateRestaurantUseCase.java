package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.exception.DomainExceptionConstants;
import com.pragma.powerup.domain.exception.FunctionalExceptionResponse;
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
            throw new OwnerNotFoundException(FunctionalExceptionResponse.BUSINESS_VALIDATION_FAILED.getMessage(),
                    Map.of(DomainExceptionConstants.OWNER_ID, FunctionalExceptionResponse.OWNER_NOT_FOUND.getMessage()));
        }
    }
}
