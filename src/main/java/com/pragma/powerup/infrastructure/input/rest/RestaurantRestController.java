package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;

    @Operation(summary = "Create restaurant", description = "Creates a new restaurant. The owner ID must correspond to a user with the OWNER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RestaurantResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Owner user not found or does not have OWNER role",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"ownerId\",\"message\":\"The provided user does not exist or does not have the owner role\"}]}"))),
           @ApiResponse(responseCode = "400", description = "Invalid fields",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"name\",\"message\":\"Name cannot consist of numbers only\"},{\"field\":\"nit\",\"message\":\"NIT must contain only digits\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    @PostMapping("/")
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@Valid @RequestBody RestaurantRequestDto restaurantRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantHandler.createRestaurant(restaurantRequestDto));
    }
}
