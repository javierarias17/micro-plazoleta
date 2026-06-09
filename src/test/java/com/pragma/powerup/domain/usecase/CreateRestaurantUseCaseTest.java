package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.OwnerNotFoundException;
import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;
import com.pragma.powerup.factory.RestaurantModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserValidationPort userValidationPort;

    @InjectMocks
    private CreateRestaurantUseCase createRestaurantUseCase;

    private RestaurantModel validRestaurant;

    @BeforeEach
    void setUp() {
        validRestaurant = RestaurantModelFactory.createValidRestaurant();
    }

    // ─── Happy path

    @Test
    void When_RestaurantDataIsCorrectAndOwnerIsValid_Expect_RestaurantToBeSavedSuccessfully() {
        // Arrange
        RestaurantModel savedRestaurant = RestaurantModelFactory.createSavedRestaurant();

        when(userValidationPort.isOwner(validRestaurant.getOwnerId())).thenReturn(true);
        when(restaurantPersistencePort.saveRestaurant(any(RestaurantModel.class))).thenReturn(savedRestaurant);

        // Act
        RestaurantModel result = createRestaurantUseCase.createRestaurant(validRestaurant);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(savedRestaurant.getName(), result.getName());
        assertEquals(savedRestaurant.getNit(), result.getNit());
        assertEquals(savedRestaurant.getOwnerId(), result.getOwnerId());
    }

    // ─── Exceptions path

    @Test
    void Expect_OwnerNotFoundException_When_UserIsNotOwner() {
        // Arrange
        when(userValidationPort.isOwner(validRestaurant.getOwnerId())).thenReturn(false);

        // Act & Assert
        assertThrows(OwnerNotFoundException.class,
                () -> createRestaurantUseCase.createRestaurant(validRestaurant));
    }

    @Test
    void Expect_TechnicalException_When_UserValidationServiceIsUnavailable() {
        // Arrange
        when(userValidationPort.isOwner(validRestaurant.getOwnerId()))
                .thenThrow(new TechnicalException("User validation service is unavailable"));

        // Act & Assert
        assertThrows(TechnicalException.class,
                () -> createRestaurantUseCase.createRestaurant(validRestaurant));
    }
}
