package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IValidateRestaurantOwnerServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.util.Optional;

public class ValidateRestaurantOwnerUseCase implements IValidateRestaurantOwnerServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public ValidateRestaurantOwnerUseCase(IRestaurantPersistencePort restaurantPersistencePort,
            IAuthenticatedUserPort authenticatedUserPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public boolean isOwner(Long restaurantId) {
        Optional<RestaurantModel> restaurant = restaurantPersistencePort.findRestaurantById(restaurantId);
        if (restaurant.isEmpty()) {
            return false;
        }
        Long authenticatedOwnerId = authenticatedUserPort.getAuthenticatedUserId();
        return restaurant.get().getOwnerId().equals(authenticatedOwnerId);
    }
}
