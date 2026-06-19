package com.pragma.powerup.domain.api;

public interface IValidateRestaurantOwnerServicePort {
    boolean isOwner(Long restaurantId);
}
