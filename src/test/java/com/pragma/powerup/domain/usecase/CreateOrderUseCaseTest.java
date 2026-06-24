package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.CustomerHasActiveOrderException;
import com.pragma.powerup.domain.exception.DishNotFromRestaurantException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.OrderDishModel;
import com.pragma.powerup.domain.model.OrderModel;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IOrderPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import com.pragma.powerup.factory.OrderModelFactory;
import com.pragma.powerup.factory.RestaurantModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    private static final long CLIENT_ID = 10L;
    private static final long RESTAURANT_ID = 1L;

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @Mock
    private ITraceabilityPort traceabilityPort;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private OrderModel validOrderRequest;
    private RestaurantModel savedRestaurant;
    private List<Long> activeDishIds;

    @BeforeEach
    void setUp() {
        validOrderRequest = OrderModelFactory.createValidOrderRequest();
        savedRestaurant = RestaurantModelFactory.createSavedRestaurant();
        activeDishIds = OrderModelFactory.createActiveDishIdsForRestaurant();
    }

    // ─── Happy path

    @Test
    void When_ValidOrderWithNoPreviousActiveOrder_Expect_OrderSavedWithPendingStatus() {
        // Arrange
        OrderModel savedOrder = OrderModelFactory.createSavedOrder();

        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.hasActiveOrderByClient(eq(CLIENT_ID), anyList())).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(dishPersistencePort.findActiveDishesByIdsAndRestaurant(anyList(), anyLong())).thenReturn(activeDishIds);
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenReturn(savedOrder);

        // Act
        OrderModel result = createOrderUseCase.createOrder(validOrderRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(OrderStatus.PENDIENTE, result.getStatus());
        assertEquals(CLIENT_ID, result.getClientId());
    }

    // ─── Exceptions path

    @Test
    void Expect_FieldsValidationException_When_RestaurantIdIsNull() {
        // Arrange
        OrderModel order = OrderModelFactory.createValidOrderRequest();
        order.setRestaurantId(null);

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createOrderUseCase.createOrder(order));
    }

    @Test
    void Expect_FieldsValidationException_When_DishesListIsNull() {
        // Arrange
        OrderModel order = OrderModelFactory.createValidOrderRequest();
        order.setDishes(null);

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createOrderUseCase.createOrder(order));
    }

    @Test
    void Expect_FieldsValidationException_When_DishesListIsEmpty() {
        // Arrange
        OrderModel order = OrderModelFactory.createValidOrderRequest();
        order.setDishes(Collections.emptyList());

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createOrderUseCase.createOrder(order));
    }

    @Test
    void Expect_FieldsValidationException_When_DishIdIsNull() {
        // Arrange
        OrderModel order = OrderModelFactory.createValidOrderRequest();
        order.setDishes(List.of(OrderDishModel.builder().dishId(null).quantity(2).build()));

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createOrderUseCase.createOrder(order));
    }

    @Test
    void Expect_FieldsValidationException_When_QuantityIsNull() {
        // Arrange
        OrderModel order = OrderModelFactory.createValidOrderRequest();
        order.setDishes(List.of(OrderDishModel.builder().dishId(1L).quantity(null).build()));

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createOrderUseCase.createOrder(order));
    }

    @Test
    void Expect_FieldsValidationException_When_QuantityIsZeroOrNegative() {
        // Arrange
        OrderModel order = OrderModelFactory.createValidOrderRequest();
        order.setDishes(List.of(OrderDishModel.builder().dishId(1L).quantity(0).build()));

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createOrderUseCase.createOrder(order));
    }

    @Test
    void Expect_CustomerHasActiveOrderException_When_ClientAlreadyHasActiveOrder() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.hasActiveOrderByClient(eq(CLIENT_ID), anyList())).thenReturn(true);

        // Act & Assert
        assertThrows(CustomerHasActiveOrderException.class,
                () -> createOrderUseCase.createOrder(validOrderRequest));
    }

    @Test
    void Expect_RestaurantNotFoundException_When_RestaurantDoesNotExist() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.hasActiveOrderByClient(eq(CLIENT_ID), anyList())).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RestaurantNotFoundException.class,
                () -> createOrderUseCase.createOrder(validOrderRequest));
    }

    @Test
    void Expect_DishNotFromRestaurantException_When_SomeDishesDoNotBelongToRestaurant() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.hasActiveOrderByClient(eq(CLIENT_ID), anyList())).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(dishPersistencePort.findActiveDishesByIdsAndRestaurant(anyList(), anyLong()))
                .thenReturn(Collections.singletonList(activeDishIds.get(0)));

        // Act & Assert
        assertThrows(DishNotFromRestaurantException.class,
                () -> createOrderUseCase.createOrder(validOrderRequest));
    }

    @Test
    void Expect_DishNotFromRestaurantException_When_NoDishesFoundForRestaurant() {
        // Arrange
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(CLIENT_ID);
        when(orderPersistencePort.hasActiveOrderByClient(eq(CLIENT_ID), anyList())).thenReturn(false);
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(dishPersistencePort.findActiveDishesByIdsAndRestaurant(anyList(), anyLong()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(DishNotFromRestaurantException.class,
                () -> createOrderUseCase.createOrder(validOrderRequest));
    }
}
