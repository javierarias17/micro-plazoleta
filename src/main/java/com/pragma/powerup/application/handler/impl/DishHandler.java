package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishMenuResponseDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import com.pragma.powerup.application.mapper.IDishMenuResponseMapper;
import com.pragma.powerup.application.mapper.IDishRequestMapper;
import com.pragma.powerup.application.mapper.IDishResponseMapper;
import com.pragma.powerup.domain.api.ICreateDishServicePort;
import com.pragma.powerup.domain.api.IListDishesServicePort;
import com.pragma.powerup.domain.api.IUpdateDishServicePort;
import com.pragma.powerup.domain.model.PagedResult;
import com.pragma.powerup.domain.model.DishModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DishHandler implements IDishHandler {

    private final ICreateDishServicePort createDishServicePort;
    private final IUpdateDishServicePort updateDishServicePort;
    private final IListDishesServicePort listDishesServicePort;
    private final IDishRequestMapper dishRequestMapper;
    private final IDishResponseMapper dishResponseMapper;
    private final IDishMenuResponseMapper dishMenuResponseMapper;

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
    public PagedResponseDto<DishMenuResponseDto> listDishes(Long restaurantId, Long categoryId, int page, int pageSize) {
        PagedResult<DishModel> pagedResult = listDishesServicePort.listDishes(restaurantId, categoryId, page, pageSize);
        PagedResponseDto<DishMenuResponseDto> response = new PagedResponseDto<>();
        response.setContent(dishMenuResponseMapper.toMenuResponseList(pagedResult.getContent()));
        response.setTotalElements(pagedResult.getTotalElements());
        response.setTotalPages(pagedResult.getTotalPages());
        response.setCurrentPage(pagedResult.getCurrentPage());
        response.setPageSize(pagedResult.getPageSize());
        return response;
    }
}
