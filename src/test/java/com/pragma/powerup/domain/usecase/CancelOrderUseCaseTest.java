package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.OrderNotCancellableException;
import com.pragma.powerup.domain.exception.OrderNotFoundException;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelOrderUseCaseTest {

    private static final long CUSTOMER_ID = 10L;
    private static final long OTHER_CUSTOMER_ID = 99L;
    private static final long ORDER_ID = 1L;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @Mock
    private ITraceabilityPort traceabilityPort;

    @InjectMocks
    private CancelOrderUseCase cancelOrderUseCase;

    private OrderModel pendingOrder;

    @BeforeEach
    void setUp() {
        pendingOrder = OrderModelFactory.createSavedOrder();
    }

    // ─── Happy path

    @Test
    void When_PendingOrderBelongsToCustomer_Expect_OrderCancelled() {
        // Arrange
        OrderModel cancelledOrder = OrderModelFactory.createCancelledOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CUSTOMER_ID);
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));
        when(orderPersistencePort.updateOrder(any(OrderModel.class))).thenReturn(cancelledOrder);

        // Act
        OrderModel result = cancelOrderUseCase.cancelOrder(ORDER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CANCELADO, result.getStatus());
    }

    // ─── Exception paths

    @Test
    void Expect_OrderNotFoundException_When_OrderDoesNotExist() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CUSTOMER_ID);
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID));
        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_ForbiddenException_When_OrderBelongsToDifferentCustomer() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OTHER_CUSTOMER_ID);
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(pendingOrder));

        // Act & Assert
        assertThrows(ForbiddenException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID));
        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_OrderNotCancellableException_When_OrderIsNotPending() {
        // Arrange
        OrderModel orderInPreparation = OrderModelFactory.createSavedOrderInPreparation();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CUSTOMER_ID);
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(orderInPreparation));

        // Act & Assert
        assertThrows(OrderNotCancellableException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID));
        verify(orderPersistencePort, never()).updateOrder(any());
    }

    @Test
    void Expect_OrderNotCancellableException_When_OrderIsAlreadyCancelled() {
        // Arrange
        OrderModel cancelledOrder = OrderModelFactory.createCancelledOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CUSTOMER_ID);
        when(orderPersistencePort.findById(ORDER_ID)).thenReturn(Optional.of(cancelledOrder));

        // Act & Assert
        assertThrows(OrderNotCancellableException.class,
                () -> cancelOrderUseCase.cancelOrder(ORDER_ID));
        verify(orderPersistencePort, never()).updateOrder(any());
    }
}
