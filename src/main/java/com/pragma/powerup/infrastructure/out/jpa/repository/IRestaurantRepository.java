package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.dto.RestaurantSummaryDto;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
    boolean existsByNit(String nit);

    @Query("SELECT new com.pragma.powerup.infrastructure.out.jpa.dto.RestaurantSummaryDto(r.id, r.name, r.urlLogo) " +
           "FROM RestaurantEntity r ORDER BY r.name ASC")
    Page<RestaurantSummaryDto> findSummaryOrderByName(Pageable pageable);
}
