package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.domain.api.ICreateDishServicePort;
import com.pragma.powerup.domain.api.IToggleDishStatusServicePort;
import com.pragma.powerup.domain.api.IUpdateDishServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final ICreateDishServicePort createDishServicePort;
    private final IUpdateDishServicePort updateDishServicePort;
    private final IToggleDishStatusServicePort toggleDishStatusServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;

    @Override
    public DishResponseDto createDish(DishRequestDto dishRequestDto) {
        return dishResponseMapper.toResponse(
                createDishServicePort.createDish(
                        dishRequestMapper.toDish(dishRequestDto)));
    }

    @Override
    public DishResponseDto updateDish(Long id, DishUpdateRequestDto dishUpdateRequestDto) {
        return dishResponseMapper.toResponse(
                updateDishServicePort.updateDish(id, dishUpdateRequestDto.getPrice(),
                        dishUpdateRequestDto.getDescription()));
    }

    @Override
    public DishResponseDto toggleDishStatus(Long id, DishStatusRequestDto dishStatusRequestDto) {
        return dishResponseMapper.toResponse(
                toggleDishStatusServicePort.toggleDishStatus(id, dishStatusRequestDto.getActive()));
    }
}
