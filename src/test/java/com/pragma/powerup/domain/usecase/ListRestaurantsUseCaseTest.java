package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.common.FieldConstants;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.factory.RestaurantModelFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRestaurantsUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @InjectMocks
    private ListRestaurantsUseCase listRestaurantsUseCase;

    private PagedResult<RestaurantModel> pagedResult;

    @BeforeEach
    void setUp() {
        pagedResult = RestaurantModelFactory.createRestaurantPagedResult();
    }

    // ─── Happy path

    @Test
    void When_ValidPageRequested_Expect_RestaurantsReturnedSortedByName() {
        // Arrange
        when(restaurantPersistencePort.findAllSortedByName(0, 3)).thenReturn(pagedResult);

        // Act
        PagedResult<RestaurantModel> result = listRestaurantsUseCase.listRestaurants(0, 3);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Asados del Valle", result.getContent().get(0).getName());
        assertEquals("La Brasa", result.getContent().get(1).getName());
        assertEquals("Zafiro Cocina", result.getContent().get(2).getName());
    }

    @Test
    void When_ValidPageRequested_Expect_PaginationMetadataReturned() {
        // Arrange
        when(restaurantPersistencePort.findAllSortedByName(0, 3)).thenReturn(pagedResult);

        // Act
        PagedResult<RestaurantModel> result = listRestaurantsUseCase.listRestaurants(0, 3);

        // Assert
        assertEquals(9L, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(0, result.getCurrentPage());
        assertEquals(3, result.getPageSize());
    }

    // ─── Exception path

    @Test
    void Expect_FieldsValidationException_When_NegativePage() {
        FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                () -> listRestaurantsUseCase.listRestaurants(-1, 10));

        assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE));
    }

    @Test
    void Expect_FieldsValidationException_When_ZeroPageSize() {
        FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                () -> listRestaurantsUseCase.listRestaurants(0, 0));

        assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE_SIZE));
    }

    @Test
    void Expect_FieldsValidationException_When_NegativePageSize() {
        FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                () -> listRestaurantsUseCase.listRestaurants(0, -5));

        assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE_SIZE));
    }

    @Test
    void Expect_FieldsValidationException_When_NegativePageAndZeroPageSize() {
        FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                () -> listRestaurantsUseCase.listRestaurants(-1, 0));

        assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE));
        assertTrue(ex.getErrors().containsKey(FieldConstants.PAGE_SIZE));
        assertEquals(2, ex.getErrors().size());
    }
}
