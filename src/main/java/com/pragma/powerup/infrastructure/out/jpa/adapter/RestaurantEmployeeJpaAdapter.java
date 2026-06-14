package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RestaurantEmployeeModel;
import com.pragma.powerup.domain.spi.IRestaurantEmployeePersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEmployeeEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEmployeeEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantEmployeeRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class RestaurantEmployeeJpaAdapter implements IRestaurantEmployeePersistencePort {

    private final IRestaurantEmployeeRepository restaurantEmployeeRepository;
    private final IRestaurantEmployeeEntityMapper restaurantEmployeeEntityMapper;

    @Override
    public boolean existsByEmployeeId(Long employeeId) {
        return restaurantEmployeeRepository.existsByEmployeeId(employeeId);
    }

    @Override
    public RestaurantEmployeeModel save(RestaurantEmployeeModel model) {
        return restaurantEmployeeEntityMapper.toModel(
                restaurantEmployeeRepository.save(
                        restaurantEmployeeEntityMapper.toEntity(model)));
    }

    @Override
    public Optional<Long> findRestaurantIdByEmployeeId(Long employeeId) {
        return restaurantEmployeeRepository.findByEmployeeId(employeeId)
                .map(RestaurantEmployeeEntity::getRestaurantId);
    }
}
