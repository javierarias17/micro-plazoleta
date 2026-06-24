package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.InvalidSecurityPinException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.OrderNotReadyException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.domain.spi.IUserServicePort;
import com.pragma.powerup.factory.OrderModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliverOrderUseCaseTest {

    private static final long EMPLOYEE_ID = 20L;
    private static final long RESTAURANT_ID = 1L;
    private static final long ORDER_ID = 1L;
    private static final long OTHER_RESTAURANT_ID = 99L;
    private static final String CORRECT_PIN = "482951";
    private static final String WRONG_PIN = "000000";
    private static final String BLANK_VALUE = "   ";

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IUserServicePort userServicePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @Mock
    private ITraceabilityPort traceabilityPort;

    @InjectMocks
    private DeliverOrderUseCase deliverOrderUseCase;

    private OrderModel readyOrder;

    @BeforeEach
    void setUp() {
        readyOrder = OrderModelFactory.createSavedReadyOrder();
    }

    // ─── Happy path

    @Test
    void When_ValidReadyOrderAndCorrectPin_Expect_OrderMarkedAsDelivered() {
        // Arrange
        OrderModel deliveredOrder = OrderModelFactory.createDeliveredOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));
        when(orderPersistencePort.updateOrder(any(OrderModel.class))).thenReturn(deliveredOrder);

        // Act
        OrderModel result = deliverOrderUseCase.deliverOrder(ORDER_ID, CORRECT_PIN);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.ENTREGADO, result.getStatus());
        verify(orderPersistencePort).updateOrder(any(OrderModel.class));
    }

    // ─── Exception paths

    @Test
    void Expect_FieldsValidationException_When_OrderIdIsNull() {
        assertThrows(FieldsValidationException.class,
                () -> deliverOrderUseCase.deliverOrder(null, CORRECT_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_FieldsValidationException_When_SecurityPinIsBlank() {
        assertThrows(FieldsValidationException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, BLANK_VALUE));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_RestaurantNotFoundException_When_EmployeeHasNoRestaurant() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, CORRECT_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_OrderNotFoundException_When_OrderDoesNotExist() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, CORRECT_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_ForbiddenException_When_OrderBelongsToDifferentRestaurant() {
        OrderModel orderFromOtherRestaurant = OrderModelFactory.createSavedOrderForRestaurant(OTHER_RESTAURANT_ID);

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderFromOtherRestaurant));

        assertThrows(ForbiddenException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, CORRECT_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_ForbiddenException_When_OrderIsAssignedToAnotherEmployee() {
        OrderModel orderAssignedToOther = OrderModelFactory.createSavedReadyOrderAssignedToOtherEmployee();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderAssignedToOther));

        assertThrows(ForbiddenException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, CORRECT_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_OrderNotReadyException_When_OrderIsNotInReadyStatus() {
        OrderModel pendingOrder = OrderModelFactory.createSavedOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));

        assertThrows(OrderNotReadyException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, CORRECT_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_InvalidSecurityPinException_When_PinDoesNotMatch() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployee(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(readyOrder));

        assertThrows(InvalidSecurityPinException.class,
                () -> deliverOrderUseCase.deliverOrder(ORDER_ID, WRONG_PIN));

        verify(orderPersistencePort, never()).updateOrder(any());
    }
}
