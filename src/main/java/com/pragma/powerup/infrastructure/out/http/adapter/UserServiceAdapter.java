package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.constant.TechnicalMessageConstants;
import com.pragma.powerup.domain.spi.IUserServicePort;
import com.pragma.powerup.infrastructure.out.http.client.IUserServiceFeignClient;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceAdapter implements IUserServicePort {

    private final IUserServiceFeignClient feignClient;

    @Override
    public boolean isOwner(Long userId) {
        try {
            return Boolean.TRUE.equals(feignClient.isOwner(userId));
        } catch (FeignException.NotFound e) {
            return false;
        } catch (RetryableException e) {
            throw new TechnicalException(TechnicalMessageConstants.USER_UNAVAILABLE);
        }
    }

    @Override
    public Optional<Long> findRestaurantIdByEmployee(Long employeeId) {
        try {
            return Optional.ofNullable(feignClient.findRestaurantIdByEmployee(employeeId));
        } catch (RetryableException e) {
            throw new TechnicalException(TechnicalMessageConstants.USER_UNAVAILABLE);
        }
    }
}
