package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IListRestaurantsServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.util.LinkedHashMap;
import java.util.Map;

public class ListRestaurantsUseCase implements IListRestaurantsServicePort {

    private static final int MIN_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 1;

    private final IRestaurantPersistencePort restaurantPersistencePort;

    public ListRestaurantsUseCase(IRestaurantPersistencePort restaurantPersistencePort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public PagedResult<RestaurantModel> listRestaurants(int page, int pageSize) {
        this.validatePaginationParams(page, pageSize);
        return restaurantPersistencePort.findAllSortedByName(page, pageSize);
    }

    private void validatePaginationParams(int page, int pageSize) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (page < MIN_PAGE)
            errors.put(FieldConstants.PAGE, FunctionalMessageConstants.PAGE_MUST_BE_ZERO_OR_POSITIVE);
        if (pageSize < MIN_PAGE_SIZE)
            errors.put(FieldConstants.PAGE_SIZE, FunctionalMessageConstants.PAGE_SIZE_MUST_BE_POSITIVE);
        if (!errors.isEmpty())
            throw new FieldsValidationException(errors);
    }
}
