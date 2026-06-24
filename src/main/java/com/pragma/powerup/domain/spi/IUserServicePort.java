package com.pragma.powerup.domain.spi;

import java.util.Optional;

public interface IUserServicePort {
    boolean isOwner(Long userId);
    Optional<Long> findRestaurantIdByEmployee(Long employeeId);
}
