package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.ICreateRestaurantServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.common.RegexConstants;
import com.pragma.powerup.domain.common.ValidationMessageConstants;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.OwnerNotFoundException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;
import com.pragma.powerup.domain.validator.FieldValidator;

import java.util.LinkedHashMap;
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
        this.validateFields(restaurantModel);
        this.validateBusinessRules(restaurantModel);
        return restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    private void validateFields(RestaurantModel restaurantModel) {
        Map<String, String> errors = new LinkedHashMap<>();

        FieldValidator.validateNotBlankAndPattern(restaurantModel.getName(), FieldConstants.NAME,
                ValidationMessageConstants.MSG_NAME_REQUIRED,
                RegexConstants.RESTAURANT_NAME_REGEX,
                ValidationMessageConstants.MSG_NAME_NOT_NUMBERS_ONLY, errors);

        FieldValidator.validateNotBlankAndPattern(restaurantModel.getNit(), FieldConstants.NIT,
                ValidationMessageConstants.MSG_NIT_REQUIRED,
                RegexConstants.NIT_REGEX,
                ValidationMessageConstants.MSG_NIT_DIGITS_ONLY, errors);

        FieldValidator.validateNotBlank(restaurantModel.getAddress(), FieldConstants.ADDRESS,
                ValidationMessageConstants.MSG_ADDRESS_REQUIRED, errors);

        FieldValidator.validateNotBlankAndPattern(restaurantModel.getPhone(), FieldConstants.PHONE,
                ValidationMessageConstants.MSG_PHONE_REQUIRED,
                RegexConstants.PHONE_REGEX,
                ValidationMessageConstants.MSG_PHONE_FORMAT, errors);

        FieldValidator.validateNotBlank(restaurantModel.getUrlLogo(), FieldConstants.URL_LOGO,
                ValidationMessageConstants.MSG_LOGO_URL_REQUIRED, errors);

        FieldValidator.validateNotNull(restaurantModel.getOwnerId(), FieldConstants.OWNER_ID,
                ValidationMessageConstants.MSG_OWNER_ID_REQUIRED, errors);

        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }

    private void validateBusinessRules(RestaurantModel restaurantModel) {
        if (!userValidationPort.isOwner(restaurantModel.getOwnerId())) {
            throw new OwnerNotFoundException(FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                    Map.of(FieldConstants.OWNER_ID, FunctionalMessageConstants.OWNER_NOT_FOUND));
        }
    }
}
