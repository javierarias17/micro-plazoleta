package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.exception.OrderNotInPreparationException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.INotifyClientPort;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotifyOrderReadyUseCaseTest {

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

    @Mock
    private INotifyClientPort notifyClientPort;

    @Mock
    private ITraceabilityPort traceabilityPort;

    @InjectMocks
    private NotifyOrderReadyUseCase notifyOrderReadyUseCase;

    private OrderModel orderInPreparation;

    @BeforeEach
    void setUp() {
        orderInPreparation = OrderModelFactory.createSavedOrderInPreparation();
    }

    // ─── Happy path

    @Test
    void When_ValidOrderInPreparation_Expect_OrderMarkedReadyAndClientNotified() {
        OrderModel readyOrder = OrderModelFactory.createReadyOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));
        when(orderPersistencePort.updateOrder(any(OrderModel.class))).thenReturn(readyOrder);

        OrderModel result = notifyOrderReadyUseCase.notifyOrderReady(ORDER_ID);

        assertNotNull(result);
        assertEquals(OrderStatus.LISTO, result.getStatus());
        assertNotNull(result.getSecurityPin());
        verify(notifyClientPort).notify(anyLong(), anyString());
    }

    // ─── Exception paths

    @Test
    void Expect_RestaurantNotFoundException_When_EmployeeHasNoRestaurant() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThrows(RestaurantNotFoundException.class,
                () -> notifyOrderReadyUseCase.notifyOrderReady(ORDER_ID));

        verify(notifyClientPort, never()).notify(anyLong(), anyString());
    }

    @Test
    void Expect_OrderNotFoundException_When_OrderDoesNotExist() {
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> notifyOrderReadyUseCase.notifyOrderReady(ORDER_ID));

        verify(notifyClientPort, never()).notify(anyLong(), anyString());
    }

    @Test
    void Expect_ForbiddenException_When_OrderBelongsToDifferentRestaurant() {
        OrderModel orderFromOtherRestaurant = OrderModelFactory.createSavedOrderForRestaurant(OTHER_RESTAURANT_ID);

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderFromOtherRestaurant));

        assertThrows(ForbiddenException.class,
                () -> notifyOrderReadyUseCase.notifyOrderReady(ORDER_ID));

        verify(notifyClientPort, never()).notify(anyLong(), anyString());
    }

    @Test
    void Expect_ForbiddenException_When_OrderIsAssignedToAnotherEmployee() {
        OrderModel orderAssignedToOther = OrderModelFactory.createSavedOrderInPreparationAssignedToOtherEmployee();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderAssignedToOther));

        assertThrows(ForbiddenException.class,
                () -> notifyOrderReadyUseCase.notifyOrderReady(ORDER_ID));

        verify(notifyClientPort, never()).notify(anyLong(), anyString());
    }

    @Test
    void Expect_OrderNotInPreparationException_When_OrderIsNotInPreparation() {
        OrderModel pendingOrder = OrderModelFactory.createSavedOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(EMPLOYEE_ID);
        when(userServicePort.findRestaurantIdByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(RESTAURANT_ID));
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));

        assertThrows(OrderNotInPreparationException.class,
                () -> notifyOrderReadyUseCase.notifyOrderReady(ORDER_ID));

        verify(notifyClientPort, never()).notify(anyLong(), anyString());
    }
}
