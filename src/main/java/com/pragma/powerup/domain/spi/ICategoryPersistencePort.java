package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.CategoryModel;

import java.util.Optional;

public interface ICategoryPersistencePort {
    Optional<CategoryModel> findCategoryById(Long id);
}
