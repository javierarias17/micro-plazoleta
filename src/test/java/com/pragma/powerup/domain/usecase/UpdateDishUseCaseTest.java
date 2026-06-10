package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.DishNotFoundException;
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
        Integer newPrice = 30000;
        String newDescription = "Updated description";

        DishModel updatedDish = DishModelFactory.createSavedDish();
        updatedDish.setPrice(newPrice);
        updatedDish.setDescription(newDescription);

        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId())).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(dishPersistencePort.updateDish(any(DishModel.class))).thenReturn(updatedDish);

        // Act
        DishModel result = updateDishUseCase.updateDish(savedDish.getId(), newPrice, newDescription);

        // Assert
        assertNotNull(result);
        assertEquals(newPrice, result.getPrice());
        assertEquals(newDescription, result.getDescription());
    }

    // ─── Exceptions path

    @Test
    void Expect_DishNotFoundException_When_DishDoesNotExist() {
        // Arrange
        when(dishPersistencePort.findDishById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DishNotFoundException.class,
                () -> updateDishUseCase.updateDish(99L, 25000, "Some description"));
    }

    @Test
    void Expect_OwnerNotAuthorizedException_When_AuthenticatedUserIsNotRestaurantOwner() {
        // Arrange
        when(dishPersistencePort.findDishById(savedDish.getId())).thenReturn(Optional.of(savedDish));
        when(restaurantPersistencePort.findRestaurantById(savedDish.getRestaurantId())).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(99L);

        // Act & Assert
        assertThrows(OwnerNotAuthorizedException.class,
                () -> updateDishUseCase.updateDish(savedDish.getId(), 30000, "New description"));
    }
}
