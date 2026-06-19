package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.exception.CategoryNotFoundException;
import com.pragma.powerup.domain.exception.FieldsValidationException;
import com.pragma.powerup.domain.exception.ForbiddenException;
import com.pragma.powerup.domain.exception.RestaurantNotFoundException;
import com.pragma.powerup.domain.model.CategoryModel;
import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IAuthenticatedUserPort;
import com.pragma.powerup.domain.spi.ICategoryPersistencePort;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.factory.CategoryModelFactory;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateDishUseCaseTest {

        private static final String BLANK_VALUE = "   ";
        private static final int PRICE_ZERO = 0;
        private static final long OTHER_USER_ID = 99L;

        @Mock
        private IDishPersistencePort dishPersistencePort;

        @Mock
        private IRestaurantPersistencePort restaurantPersistencePort;

        @Mock
        private ICategoryPersistencePort categoryPersistencePort;

        @Mock
        private IAuthenticatedUserPort authenticatedUserPort;

        @InjectMocks
        private CreateDishUseCase createDishUseCase;

        private DishModel validDish;
        private RestaurantModel savedRestaurant;
        private CategoryModel savedCategory;

        @BeforeEach
        void setUp() {
                validDish = DishModelFactory.createValidDish();
                savedRestaurant = RestaurantModelFactory.createSavedRestaurant();
                savedCategory = CategoryModelFactory.createSavedCategory();
        }

        // ─── Happy path

        @Test
        void When_DishDataIsCorrectAndRestaurantAndCategoryExist_Expect_DishToBeSavedWithActiveTrue() {
                // Arrange
                DishModel savedDish = DishModelFactory.createSavedDish();

                when(restaurantPersistencePort.findRestaurantById(validDish.getRestaurantId()))
                                .thenReturn(Optional.of(savedRestaurant));
                when(categoryPersistencePort.findCategoryById(validDish.getCategoryId()))
                                .thenReturn(Optional.of(savedCategory));
                when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(savedRestaurant.getOwnerId());
                when(dishPersistencePort.saveDish(any(DishModel.class))).thenReturn(savedDish);

                // Act
                DishModel result = createDishUseCase.createDish(validDish);

                // Assert
                assertNotNull(result);
                assertNotNull(result.getId());
                assertEquals(savedDish.getName(), result.getName());
                assertEquals(savedDish.getPrice(), result.getPrice());
                assertTrue(result.getActive());
        }

        // ─── Exceptions path

        @Test
        void Expect_FieldsValidationException_When_NameIsBlank() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setName(BLANK_VALUE);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_FieldsValidationException_When_CategoryIdIsNull() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setCategoryId(null);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_FieldsValidationException_When_DescriptionIsBlank() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setDescription(BLANK_VALUE);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_FieldsValidationException_When_PriceIsNull() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setPrice(null);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_FieldsValidationException_When_PriceIsZeroOrNegative() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setPrice(PRICE_ZERO);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_FieldsValidationException_When_RestaurantIdIsNull() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setRestaurantId(null);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_FieldsValidationException_When_UrlImageIsBlank() {
                // Arrange
                DishModel dish = DishModelFactory.createValidDish();
                dish.setUrlImage(BLANK_VALUE);

                // Act & Assert
                assertThrows(FieldsValidationException.class,
                                () -> createDishUseCase.createDish(dish));
        }

        @Test
        void Expect_RestaurantNotFoundException_When_RestaurantDoesNotExist() {
                // Arrange
                when(restaurantPersistencePort.findRestaurantById(validDish.getRestaurantId()))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(RestaurantNotFoundException.class,
                                () -> createDishUseCase.createDish(validDish));
        }

        @Test
        void Expect_CategoryNotFoundException_When_CategoryDoesNotExist() {
                // Arrange
                when(restaurantPersistencePort.findRestaurantById(validDish.getRestaurantId()))
                                .thenReturn(Optional.of(savedRestaurant));
                when(categoryPersistencePort.findCategoryById(validDish.getCategoryId()))
                                .thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(CategoryNotFoundException.class,
                                () -> createDishUseCase.createDish(validDish));
        }

        @Test
        void Expect_ForbiddenException_When_AuthenticatedUserIsNotRestaurantOwner() {
                // Arrange
                when(restaurantPersistencePort.findRestaurantById(validDish.getRestaurantId()))
                                .thenReturn(Optional.of(savedRestaurant));
                when(categoryPersistencePort.findCategoryById(validDish.getCategoryId()))
                                .thenReturn(Optional.of(savedCategory));
                when(authenticatedUserPort.getAuthenticatedUserId()).thenReturn(OTHER_USER_ID);

                // Act & Assert
                assertThrows(ForbiddenException.class,
                                () -> createDishUseCase.createDish(validDish));
        }
}
