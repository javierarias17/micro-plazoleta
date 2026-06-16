package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.FieldsValidationException;
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
    void Expect_FieldsValidationException_When_NameIsBlank() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setName("   ");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_NameIsOnlyNumbers() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setName("123456");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_NitIsBlank() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setNit("   ");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_NitContainsNonDigits() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setNit("90ABC123");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_AddressIsBlank() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setAddress("   ");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_PhoneIsBlank() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setPhone("   ");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_PhoneHasInvalidFormat() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setPhone("++5730012345678901234");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_UrlLogoIsBlank() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setUrlLogo("   ");

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

    @Test
    void Expect_FieldsValidationException_When_OwnerIdIsNull() {
        // Arrange
        RestaurantModel restaurant = RestaurantModelFactory.createValidRestaurant();
        restaurant.setOwnerId(null);

        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant));
    }

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
