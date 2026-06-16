package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.factory.DishModelFactory;
import com.pragma.powerup.factory.RestaurantModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ToggleDishStatusUseCaseTest {

    private static final long VALID_DISH_ID = 1L;
    private static final long OTHER_ID = 99L;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private ToggleDishStatusUseCase toggleDishStatusUseCase;

    private DishModel savedDish;
    private RestaurantModel savedRestaurant;

    @BeforeEach
    void setUp() {
        savedDish = DishModelFactory.createSavedDish();
        savedRestaurant = RestaurantModelFactory.createSavedRestaurant();
    }

    // ─── Happy path

    @Test
    void When_DishExistsAndUserIsOwner_Expect_DishToBeDisabled() {
        // Arrange
        DishModel disabledDish = DishModelFactory.createSavedDish();
        disabledDish.setActive(false);

        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId()))
                .thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(dishPersistencePort.updateDish(any(DishModel.class))).thenReturn(disabledDish);

        // Act
        DishModel result = toggleDishStatusUseCase.toggleDishStatus(savedDish.getId(), false);

        // Assert
        assertNotNull(result);
        assertFalse(result.getActive());
    }

    @Test
    void When_DishExistsAndUserIsOwner_Expect_DishToBeEnabled() {
        // Arrange
        savedDish.setActive(false);
        DishModel enabledDish = DishModelFactory.createSavedDish();
        enabledDish.setActive(true);

        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId()))
                .thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(dishPersistencePort.updateDish(any(DishModel.class))).thenReturn(enabledDish);

        // Act
        DishModel result = toggleDishStatusUseCase.toggleDishStatus(savedDish.getId(), true);

        // Assert
        assertNotNull(result);
        assertTrue(result.getActive());
    }

    // ─── Exception path

    @Test
    void Expect_FieldsValidationException_When_IdIsNull() {
        assertThrows(FieldsValidationException.class,
                () -> toggleDishStatusUseCase.toggleDishStatus(null, false));
    }

    @Test
    void Expect_FieldsValidationException_When_ActiveIsNull() {
        assertThrows(FieldsValidationException.class,
                () -> toggleDishStatusUseCase.toggleDishStatus(VALID_DISH_ID, null));
    }

    @Test
    void Expect_FieldsValidationException_When_BothFieldsAreNull() {
        assertThrows(FieldsValidationException.class,
                () -> toggleDishStatusUseCase.toggleDishStatus(null, null));
    }

    @Test
    void Expect_DishNotFoundException_When_DishDoesNotExist() {
        // Arrange
        when(dishPersistencePort.findDishById(OTHER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DishNotFoundException.class,
                () -> toggleDishStatusUseCase.toggleDishStatus(OTHER_ID, false));
    }

    @Test
    void Expect_ForbiddenException_When_AuthenticatedUserIsNotRestaurantOwner() {
        // Arrange
        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId()))
                .thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OTHER_ID);

        // Act & Assert
        assertThrows(ForbiddenException.class,
                () -> toggleDishStatusUseCase.toggleDishStatus(savedDish.getId(), false));
    }
}
