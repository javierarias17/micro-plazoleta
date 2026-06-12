package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishStatusRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.handler.IDishHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/dish")
@RequiredArgsConstructor
public class DishRestController implements IDishRestControllerDocs {

    private final IDishHandler dishHandler;

    @Override
    @PostMapping
    public ResponseEntity<DishResponseDto> createDish(@Valid @RequestBody DishRequestDto dishRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dishHandler.createDish(dishRequestDto));
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<DishResponseDto> updateDish(@PathVariable Long id,
            @Valid @RequestBody DishUpdateRequestDto dishUpdateRequestDto) {
        return ResponseEntity.ok(dishHandler.updateDish(id, dishUpdateRequestDto));
    }

    @Override
    @PatchMapping("/{id}/status")
    public ResponseEntity<DishResponseDto> toggleDishStatus(@PathVariable Long id,
            @Valid @RequestBody DishStatusRequestDto dishStatusRequestDto) {
        return ResponseEntity.ok(dishHandler.toggleDishStatus(id, dishStatusRequestDto));
    }
}
