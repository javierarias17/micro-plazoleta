package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PagedResult;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListDishesUseCaseTest {

        @Mock
        private IDishPersistencePort dishPersistencePort;

        @Mock
        private IRestaurantPersistencePort restaurantPersistencePort;

        @InjectMocks
        private ListDishesUseCase listDishesUseCase;

        private PagedResult<DishModel> pagedResult;

        @BeforeEach
        void setUp() {
                pagedResult = DishModelFactory.createDishPagedResult();
        }

        // ─── Happy path

        @Test
        void When_ValidRequest_Expect_DishesReturned() {
                // Arrange
                when(restaurantPersistencePort.findRestaurantById(1L))
                                .thenReturn(Optional.of(RestaurantModelFactory.createSavedRestaurant()));
                when(dishPersistencePort.findDishesByRestaurant(1L, null, 0, 3)).thenReturn(pagedResult);

                // Act
                PagedResult<DishModel> result = listDishesUseCase.listDishes(1L, null, 0, 3);

                // Assert
                assertNotNull(result);
                assertEquals(3, result.getContent().size());
                assertEquals(6L, result.getTotalElements());
                assertEquals(2, result.getTotalPages());
                assertEquals(0, result.getCurrentPage());
                assertEquals(3, result.getPageSize());
        }

        @Test
        void When_FilteredByCategory_Expect_OnlyCategoryDishesReturned() {
                // Arrange
                PagedResult<DishModel> filtered = PagedResult.<DishModel>builder()
                                .content(pagedResult.getContent().stream()
                                                .filter(d -> d.getCategoryId().equals(1L)).toList())
                                .totalElements(2L).totalPages(1).currentPage(0).pageSize(3)
                                .build();
                when(restaurantPersistencePort.findRestaurantById(1L))
                                .thenReturn(Optional.of(RestaurantModelFactory.createSavedRestaurant()));
                when(dishPersistencePort.findDishesByRestaurant(1L, 1L, 0, 3)).thenReturn(filtered);

                // Act
                PagedResult<DishModel> result = listDishesUseCase.listDishes(1L, 1L, 0, 3);

                // Assert
                assertEquals(2, result.getContent().size());
                assertTrue(result.getContent().stream().allMatch(d -> d.getCategoryId().equals(1L)));
        }

        // ─── Exceptions path

        @Test
        void Expect_FieldsValidationException_WhenNegativePage() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listDishesUseCase.listDishes(1L, null, -1, 10));

                assertTrue(ex.getErrors().containsKey("page"));
        }

        @Test
        void Expect_FieldsValidationException_When_ZeroPageSize() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listDishesUseCase.listDishes(1L, null, 0, 0));

                assertTrue(ex.getErrors().containsKey("pageSize"));
        }

        @Test
        void Expect_FieldsValidationException_When_NegativePageAndZeroPageSize() {
                FieldsValidationException ex = assertThrows(FieldsValidationException.class,
                                () -> listDishesUseCase.listDishes(1L, null, -1, 0));

                assertTrue(ex.getErrors().containsKey("page"));
                assertTrue(ex.getErrors().containsKey("pageSize"));
                assertEquals(2, ex.getErrors().size());
        }

        @Test
        void Expect_RestaurantNotFoundException_When_RestaurantNotExist() {
                when(restaurantPersistencePort.findRestaurantById(99L)).thenReturn(Optional.empty());

                assertThrows(RestaurantNotFoundException.class,
                                () -> listDishesUseCase.listDishes(99L, null, 0, 10));
        }
}