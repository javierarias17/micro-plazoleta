package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.factory.RestaurantModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateRestaurantOwnerUseCaseTest {

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_USER_ID = 99L;
    private static final Long RESTAURANT_ID = 1L;
    private static final Long NON_EXISTENT_RESTAURANT_ID = 999L;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private ValidateRestaurantOwnerUseCase validateRestaurantOwnerUseCase;

    private RestaurantModel savedRestaurant;

    @BeforeEach
    void setUp() {
        savedRestaurant = RestaurantModelFactory.createSavedRestaurant();
    }

    // ─── Happy path

    @Test
    void When_RestaurantExistsAndAuthenticatedUserIsOwner_Expect_TrueReturned() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OWNER_ID);

        // Act
        boolean result = validateRestaurantOwnerUseCase.isOwner(RESTAURANT_ID);

        // Assert
        assertTrue(result);
    }

    // ─── Exceptions path

    @Test
    void When_RestaurantExistsButAuthenticatedUserIsNotOwner_Expect_FalseReturned() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OTHER_USER_ID);

        // Act
        boolean result = validateRestaurantOwnerUseCase.isOwner(RESTAURANT_ID);

        // Assert
        assertFalse(result);
    }

    @Test
    void When_RestaurantDoesNotExist_Expect_FalseReturned() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(NON_EXISTENT_RESTAURANT_ID)).thenReturn(Optional.empty());

        // Act
        boolean result = validateRestaurantOwnerUseCase.isOwner(NON_EXISTENT_RESTAURANT_ID);

        // Assert
        assertFalse(result);
    }
}
