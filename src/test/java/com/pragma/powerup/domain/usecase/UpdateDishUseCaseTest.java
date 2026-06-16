package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DishNotFoundException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.OwnerNotAuthorizedException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateDishUseCaseTest {

    private static final int NEW_PRICE = 30000;
    private static final String NEW_DESCRIPTION = "Updated description";
    private static final int VALID_PRICE = 25000;
    private static final String VALID_DESCRIPTION = "Some description";
    private static final String BLANK_VALUE = "   ";
    private static final int PRICE_ZERO = 0;
    private static final long OTHER_ID = 99L;

    @Mock
    private IDishPersistencePort dishPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private UpdateDishUseCase updateDishUseCase;

    private DishModel savedDish;
    private RestaurantModel savedRestaurant;

    @BeforeEach
    void setUp() {
        savedDish = DishModelFactory.createSavedDish();
        savedRestaurant = RestaurantModelFactory.createSavedRestaurant();
    }

    // ─── Happy path

    @Test
    void When_DishExistsAndUserIsOwner_Expect_DishToBeUpdated() {
        // Arrange
        DishModel updatedDish = DishModelFactory.createSavedDish();
        updatedDish.setPrice(NEW_PRICE);
        updatedDish.setDescription(NEW_DESCRIPTION);

        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId()))
                .thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(dishPersistencePort.updateDish(any(DishModel.class))).thenReturn(updatedDish);

        // Act
        DishModel result = updateDishUseCase.updateDish(savedDish.getId(), NEW_PRICE, NEW_DESCRIPTION);

        // Assert
        assertNotNull(result);
        assertEquals(NEW_PRICE, result.getPrice());
        assertEquals(NEW_DESCRIPTION, result.getDescription());
    }

    // ─── Exceptions path

    @Test
    void Expect_FieldsValidationException_When_PriceIsNull() {
        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> updateDishUseCase.updateDish(savedDish.getId(), null, VALID_DESCRIPTION));
    }

    @Test
    void Expect_FieldsValidationException_When_PriceIsZeroOrNegative() {
        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> updateDishUseCase.updateDish(savedDish.getId(), PRICE_ZERO, VALID_DESCRIPTION));
    }

    @Test
    void Expect_FieldsValidationException_When_DescriptionIsBlank() {
        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> updateDishUseCase.updateDish(savedDish.getId(), VALID_PRICE, BLANK_VALUE));
    }

    @Test
    void Expect_DishNotFoundException_When_DishDoesNotExist() {
        // Arrange
        when(dishPersistencePort.findDishById(OTHER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DishNotFoundException.class,
                () -> updateDishUseCase.updateDish(OTHER_ID, VALID_PRICE, VALID_DESCRIPTION));
    }

    @Test
    void Expect_OwnerNotAuthorizedException_When_AuthenticatedUserIsNotRestaurantOwner() {
        // Arrange
        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId()))
                .thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OTHER_ID);

        // Act & Assert
        assertThrows(OwnerNotAuthorizedException.class,
                () -> updateDishUseCase.updateDish(savedDish.getId(), NEW_PRICE, NEW_DESCRIPTION));
    }
}
