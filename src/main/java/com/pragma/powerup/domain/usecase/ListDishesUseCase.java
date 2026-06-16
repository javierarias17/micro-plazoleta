package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IListDishesServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;

import java.util.LinkedHashMap;
import java.util.Map;

public class ListDishesUseCase implements IListDishesServicePort {

    private static final int MIN_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 1;

    private final IDishPersistencePort dishPersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public ListDishesUseCase(IDishPersistencePort dishPersistencePort,
            IRestaurantPersistencePort restaurantPersistencePort) {
        this.dishPersistencePort = dishPersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public PagedResult<DishModel> listDishes(Long restaurantId, Long categoryId, int page, int pageSize) {
        this.validatePaginationParams(page, pageSize);
        restaurantPersistencePort.findRestaurantById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.RESTAURANT_ID, FunctionalMessageConstants.RESTAURANT_NOT_FOUND)));
        return dishPersistencePort.findDishesByRestaurant(restaurantId, categoryId, page, pageSize);
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
