package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ILinkEmployeeServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.EmployeeAlreadyLinkedException;
import com.pragma.powerup.domain.exception.ForbiddenException;

import com.pragma.powerup.domain.exception.OwnerNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkEmployeeUseCase implements ILinkEmployeeServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;
    private final IUserValidationPort userValidationPort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public LinkEmployeeUseCase(IRestaurantPersistencePort restaurantPersistencePort,
                               IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort,
                               IUserValidationPort userValidationPort,
                               IAuthenticatedUserPort authenticatedUserPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.restaurantEmployeePersistencePort = restaurantEmployeePersistencePort;
        this.userValidationPort = userValidationPort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public RestaurantEmployeeModel linkEmployee(Long restaurantId, Long employeeId) {
        this.validateForLinkEmployee(restaurantId, employeeId);
        this.validateBusinessRules(restaurantId, employeeId);

        return restaurantEmployeePersistencePort.save(
                RestaurantEmployeeModel.builder()
                        .employeeId(employeeId)
                        .restaurantId(restaurantId)
                        .build());
    }

    private void validateForLinkEmployee(Long restaurantId, Long employeeId) {
        Map<String, String> errors = new LinkedHashMap<>();

        if (restaurantId == null)
            errors.put(FieldConstants.RESTAURANT_ID, ValidationMessageConstants.MSG_RESTAURANT_ID_REQUIRED);

        if (employeeId == null)
            errors.put(FieldConstants.EMPLOYEE_ID, ValidationMessageConstants.MSG_EMPLOYEE_ID_REQUIRED);

        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }

    private void validateBusinessRules(Long restaurantId, Long employeeId) {
        RestaurantModel restaurant = restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.RESTAURANT_ID, FunctionalMessageConstants.RESTAURANT_NOT_FOUND)));

        Long authenticatedOwnerId = authenticatedUserPort.getAuthenticatedUserId();
        if (!restaurant.getOwnerId().equals(authenticatedOwnerId)) {
            throw new ForbiddenException();
        }

        if (!userValidationPort.isEmployee(employeeId)) {
            throw new OwnerNotFoundException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.EMPLOYEE_ID, FunctionalMessageConstants.OWNER_NOT_FOUND));
        }

        if (restaurantEmployeePersistencePort.existsByEmployeeId(employeeId)) {
            throw new EmployeeAlreadyLinkedException(
                    FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.EMPLOYEE_ID, FunctionalMessageConstants.EMPLOYEE_ALREADY_LINKED));
        }
    }

}