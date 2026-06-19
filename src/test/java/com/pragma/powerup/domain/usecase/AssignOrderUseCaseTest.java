package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.OrderNotAssignableException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IUserServicePort;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignOrderUseCaseTest {

    private static final long EMPLOYEE_ID = 20L;
    private static final long RESTAURANT_ID = 1L;
    private static final long ORDER_ID = 1L;
    private static final long OTHER_RESTAURANT_ID = 99L;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private AssignOrderUseCase assignOrderUseCase;

    private OrderModel pendingOrder;

    @BeforeEach
    void setUp() {
        pendingOrder = OrderModelFactory.createSavedOrder();
    }

    // ─── Happy path

    @Test
    void When_ValidAssignment_Expect_OrderUpdatedWithChefAndEnPreparacionStatus() {
        // Arrange
        OrderModel assignedOrder = OrderModelFactory.createAssignedOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
        when(orderPersistencePort.updateOrder(any(OrderModel.class))).thenReturn(assignedOrder);

        // Act
        OrderModel result = assignOrderUseCase.assignOrder(ORDER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(EMPLOYEE_ID, result.getChefId());
        assertEquals(OrderStatus.EN_PREPARACION, result.getStatus());
    }

    // ─── Exceptions path

    @Test
    void Expect_RestaurantNotFoundException_When_EmployeeHasNoRestaurant() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RestaurantNotFoundException.class,
                () -> assignOrderUseCase.assignOrder(ORDER_ID));
    }

    @Test
    void Expect_OrderNotFoundException_When_OrderDoesNotExist() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> assignOrderUseCase.assignOrder(ORDER_ID));
    }

    @Test
    void Expect_ForbiddenException_When_OrderBelongsToDifferentRestaurant() {
        // Arrange
        OrderModel orderFromOtherRestaurant = OrderModelFactory.createSavedOrderForRestaurant(OTHER_RESTAURANT_ID);

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderFromOtherRestaurant));

        // Act & Assert
        assertThrows(ForbiddenException.class,
                () -> assignOrderUseCase.assignOrder(ORDER_ID));
    }

    @Test
    void Expect_OrderNotAssignableException_When_OrderIsNotInPendienteStatus() {
        // Arrange
        OrderModel orderInPreparacion = OrderModelFactory.createSavedOrderInEnPreparacion();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID))
                .thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparacion));

        // Act & Assert
        assertThrows(OrderNotAssignableException.class,
                () -> assignOrderUseCase.assignOrder(ORDER_ID));
    }
}
