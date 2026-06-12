package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;

public interface IDishHandler {
    DishResponseDto createDish(DishRequestDto dishRequestDto);
    DishResponseDto updateDish(Long id, DishUpdateRequestDto dishUpdateRequestDto);
    DishResponseDto toggleDishStatus(Long id, DishStatusRequestDto dishStatusRequestDto);
}
