package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishMenuResponseDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;

public interface IDishHandler {
    DishResponseDto createDish(DishRequestDto dishRequestDto);

    DishResponseDto updateDish(Long id, DishUpdateRequestDto dishUpdateRequestDto);

    PagedResponseDto<DishMenuResponseDto> listDishes(Long restaurantId, Long categoryId, int page, int pageSize);

    DishResponseDto toggleDishStatus(Long id, DishStatusRequestDto dishStatusRequestDto);
}
