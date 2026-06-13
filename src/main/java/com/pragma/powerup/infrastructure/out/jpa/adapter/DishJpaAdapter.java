package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.DishModel;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.spi.IDishPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IDishEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;

    @Override
    public DishModel saveDish(DishModel dishModel) {
        return dishEntityMapper.toDishModel(
                dishRepository.save(dishEntityMapper.toEntity(dishModel)));
    }

    @Override
    public Optional<DishModel> findDishById(Long id) {
        return dishRepository.findById(id).map(dishEntityMapper::toDishModel);
    }

    @Override
    public DishModel updateDish(DishModel dishModel) {
        return dishEntityMapper.toDishModel(
                dishRepository.save(dishEntityMapper.toEntity(dishModel)));
    }

    @Override
    public PagedResult<DishModel> findDishesByRestaurant(Long restaurantId, Long categoryId, int page, int pageSize) {
        PageRequest pageable = PageRequest.of(page, pageSize);
        Page<DishEntity> dishPage = categoryId != null
                ? dishRepository.findByRestaurant_IdAndCategory_IdAndActiveTrue(restaurantId, categoryId, pageable)
                : dishRepository.findByRestaurant_IdAndActiveTrue(restaurantId, pageable);

        List<DishModel> content = dishPage.getContent().stream()
                .map(dishEntityMapper::toDishModel)
                .toList();

        return new PagedResult<>(content, dishPage.getTotalElements(),
                dishPage.getTotalPages(), dishPage.getNumber(), dishPage.getSize());
    }
}
