package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.EmployeeNotLinkedToRestaurantException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.factory.OrderModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListOrdersUseCaseTest {

        @Mock
        private IOrderPersistencePort orderPersistencePort;

        @Mock
        private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

        @Mock
        private IAuthenticatedUserPort authenticatedUserPort;

        @InjectMocks
        private ListOrdersUseCase listOrdersUseCase;

        private static final Long EMPLOYEE_ID = 20L;
        private static final Long RESTAURANT_ID = 1L;
        private static final int DEFAULT_PAGE = 0;
        private static final int DEFAULT_PAGE_SIZE = 10;
        private static final int NEGATIVE_PAGE = -1;
        private static final int ZERO_PAGE_SIZE = 0;
        private static final int NEGATIVE_PAGE_SIZE = -5;

        private PagedResult<OrderModel> pagedResult;

        @BeforeEach
        void setUp() {
                pagedResult = OrderModelFactory.createOrderPagedResult();
        }

        // ─── Happy path

        @Test
        void When_ValidRequestFromLinkedEmployee_Expect_PagedOrdersReturned() {
                // Arrange
                when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
                when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                                .thenReturn(Optional.of(RESTAURANT_ID));
                when(orderPersistencePort.findByRestaurantAndStatus(anyLong(), any(OrderStatus.class), anyInt(),
                                anyInt()))
                                .thenReturn(pagedResult);

                // Act
                PagedResult<OrderModel> result = listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, DEFAULT_PAGE,
                                DEFAULT_PAGE_SIZE);

                // Assert
                assertNotNull(result);
                assertEquals(2, result.getContent().size());
                assertEquals(2L, result.getTotalElements());
                assertEquals(1, result.getTotalPages());
                assertEquals(DEFAULT_PAGE, result.getCurrentPage());
                assertEquals(DEFAULT_PAGE_SIZE, result.getPageSize());
        }

        @Test
        void When_ValidRequestFromLinkedEmployee_Expect_AllOrdersHaveCorrectStatus() {
                // Arrange
                when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
                when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                                .thenReturn(Optional.of(RESTAURANT_ID));
                when(orderPersistencePort.findByRestaurantAndStatus(anyLong(), any(OrderStatus.class), anyInt(),
                                anyInt()))
                                .thenReturn(pagedResult);

                // Act
                PagedResult<OrderModel> result = listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, DEFAULT_PAGE,
                                DEFAULT_PAGE_SIZE);

                // Assert
                result.getContent().forEach(order -> assertEquals(OrderStatus.PENDIENTE, order.getStatus()));
        }

        // ─── Exceptions path

        @Test
        void Expect_FieldsValidationException_When_NegativePage() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, NEGATIVE_PAGE,
                                                DEFAULT_PAGE_SIZE));

                assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE));
        }

        @Test
        void Expect_FieldsValidationException_When_ZeroPageSize() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, DEFAULT_PAGE,
                                                ZERO_PAGE_SIZE));

                assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE_SIZE));
        }

        @Test
        void Expect_FieldsValidationException_When_NegativePageSize() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, DEFAULT_PAGE,
                                                NEGATIVE_PAGE_SIZE));

                assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE_SIZE));
        }

        @Test
        void Expect_FieldsValidationException_When_NegativePageAndZeroPageSize() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, NEGATIVE_PAGE,
                                                ZERO_PAGE_SIZE));

                assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE));
                assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE_SIZE));
                assertEquals(2, ex.getErrors().size());
        }

        @Test
        void Expect_EmployeeNotLinkedToRestaurantException_When_EmployeeHasNoRestaurant() {
                // Arrange
                when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
                when(restaurantEmployeePersistencePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(EmployeeNotLinkedToRestaurantException.class,
                                () -> listOrdersUseCase.listOrders(OrderStatus.PENDIENTE, DEFAULT_PAGE,
                                                DEFAULT_PAGE_SIZE));
        }
}
