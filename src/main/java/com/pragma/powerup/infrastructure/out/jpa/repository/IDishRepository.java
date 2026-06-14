package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {
    Page<DishEntity> findByRestaurant_IdAndActiveTrue(Long restaurantId, Pageable pageable);
    Page<DishEntity> findByRestaurant_IdAndCategory_IdAndActiveTrue(Long restaurantId, Long categoryId, Pageable pageable);
    @Query("SELECT d.id FROM DishEntity d WHERE d.id IN :ids AND d.restaurant.id = :restaurantId AND d.active = true")
    List<Long> findIdsByIdInAndRestaurantIdAndActiveTrue(@Param("ids") List<Long> ids, @Param("restaurantId") Long restaurantId);
}
