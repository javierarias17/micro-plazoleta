package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IListOrdersServicePort;
import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.constant.FunctionalMessageConstants;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;


import java.util.LinkedHashMap;
import java.util.Map;

public class ListOrdersUseCase implements IListOrdersServicePort {

    private static final int MIN_PAGE = 0;
    private static final int MIN_PAGE_SIZE = 1;

    private final IOrderPersistencePort orderPersistencePort;
    private final IUserServicePort userServicePort;
    private final IAuthenticatedUserPort authenticatedUserPort;

    public ListOrdersUseCase(IOrderPersistencePort orderPersistencePort,
            IUserServicePort userServicePort,
            IAuthenticatedUserPort authenticatedUserPort) {
        this.orderPersistencePort = orderPersistencePort;
        this.userServicePort = userServicePort;
        this.authenticatedUserPort = authenticatedUserPort;
    }

    @Override
    public PagedResult<OrderModel> listOrders(OrderStatus status, int page, int pageSize) {
        this.validatePaginationParams(page, pageSize);

        Long restaurantId = userServicePort.findRestaurantIdByEmployee(authenticatedUserPort.getAuthenticatedUserId())
                .orElseThrow(() -> new EmployeeNotLinkedToRestaurantException(
                        FunctionalMessageConstants.BUSINESS_VALIDATION_FAILED,
                        Map.of(FieldConstants.EMPLOYEE_ID,
                                FunctionalMessageConstants.EMPLOYEE_NOT_LINKED_TO_RESTAURANT)));

        return orderPersistencePort.findByRestaurantAndStatus(restaurantId, status, page, pageSize);
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
