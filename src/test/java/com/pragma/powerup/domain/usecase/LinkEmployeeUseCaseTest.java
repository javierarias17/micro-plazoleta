package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.EmployeeAlreadyLinkedException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.OwnerNotFoundException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserValidationPort;
import com.pragma.powerup.factory.RestaurantModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkEmployeeUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IRestaurantEmployeePersistencePort restaurantEmployeePersistencePort;

    @Mock
    private IUserValidationPort userValidationPort;

    @Mock
    private IAuthenticatedUserPort authenticatedUserPort;

    @InjectMocks
    private LinkEmployeeUseCase linkEmployeeUseCase;

    private RestaurantModel savedRestaurant;
    private static final Long RESTAURANT_ID = 1L;
    private static final Long EMPLOYEE_ID = 10L;

    @BeforeEach
    void setUp() {
        savedRestaurant = RestaurantModelFactory.createSavedRestaurant();
    }

    // ─── Happy path

    @Test
    void When_InformationIsCorrect_Expect_EmployeeToBeLinkedSuccessfully() {
        // Arrange
        RestaurantEmployeeModel savedLink = RestaurantEmployeeModel.builder()
                .id(1L)
                .restaurantId(RESTAURANT_ID)
                .employeeId(EMPLOYEE_ID)
                .build();

        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(userValidationPort.isEmployee(EMPLOYEE_ID)).thenReturn(true);
        when(restaurantEmployeePersistencePort.existsByEmployeeId(EMPLOYEE_ID)).thenReturn(false);
        when(restaurantEmployeePersistencePort.save(any(RestaurantEmployeeModel.class))).thenReturn(savedLink);

        // Act
        RestaurantEmployeeModel result = linkEmployeeUseCase.linkEmployee(RESTAURANT_ID, EMPLOYEE_ID);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(RESTAURANT_ID, result.getRestaurantId());
        assertEquals(EMPLOYEE_ID, result.getEmployeeId());
    }

    // ─── Field validation exceptions

    @Test
    void Expect_FieldsValidationException_When_RestaurantIdIsNull() {
        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> linkEmployeeUseCase.linkEmployee(null, EMPLOYEE_ID));
    }

    @Test
    void Expect_FieldsValidationException_When_EmployeeIdIsNull() {
        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> linkEmployeeUseCase.linkEmployee(RESTAURANT_ID, null));
    }

    @Test
    void Expect_FieldsValidationException_When_BothIdsAreNull() {
        // Act & Assert
        assertThrows(FieldsValidationException.class,
                () -> linkEmployeeUseCase.linkEmployee(null, null));
    }

    // ─── Business rule exceptions

    @Test
    void Expect_RestaurantNotFoundException_When_RestaurantDoesNotExist() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RestaurantNotFoundException.class,
                () -> linkEmployeeUseCase.linkEmployee(RESTAURANT_ID, EMPLOYEE_ID));
    }

    @Test
    void Expect_ForbiddenException_When_AuthenticatedUserIsNotRestaurantOwner() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(99L);

        // Act & Assert
        assertThrows(ForbiddenException.class,
                () -> linkEmployeeUseCase.linkEmployee(RESTAURANT_ID, EMPLOYEE_ID));
    }

    @Test
    void Expect_OwnerNotFoundException_When_UserIsNotAnEmployee() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(userValidationPort.isEmployee(EMPLOYEE_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(OwnerNotFoundException.class,
                () -> linkEmployeeUseCase.linkEmployee(RESTAURANT_ID, EMPLOYEE_ID));
    }

    @Test
    void Expect_EmployeeAlreadyLinkedException_When_EmployeeIsAlreadyLinkedToARestaurant() {
        // Arrange
        when(restaurantPersistencePort.findRestaurantById(RESTAURANT_ID)).thenReturn(Optional.of(savedRestaurant));
        when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
        when(userValidationPort.isEmployee(EMPLOYEE_ID)).thenReturn(true);
        when(restaurantEmployeePersistencePort.existsByEmployeeId(EMPLOYEE_ID)).thenReturn(true);

        // Act & Assert
        assertThrows(EmployeeAlreadyLinkedException.class,
                () -> linkEmployeeUseCase.linkEmployee(RESTAURANT_ID, EMPLOYEE_ID));
    }
}
