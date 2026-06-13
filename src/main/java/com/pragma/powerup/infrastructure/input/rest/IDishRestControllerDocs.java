package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.DishRequestDto;
import com.pragma.powerup.application.dto.request.DishUpdateRequestDto;
import com.pragma.powerup.application.dto.response.DishMenuResponseDto;
import com.pragma.powerup.application.dto.response.DishResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface IDishRestControllerDocs {

    @Operation(summary = "Create dish", description = "Creates a new dish for a restaurant. The authenticated user must be the owner of the given restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid fields", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"price\",\"message\":\"Price must be a positive number\"}]}"))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not the owner of the restaurant", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"ownerId\",\"message\":\"You are not authorized to create dishes for this restaurant\"}]}"))),
            @ApiResponse(responseCode = "404", description = "Restaurant or category not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"restaurantId\",\"message\":\"The restaurant does not exist\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<DishResponseDto> createDish(DishRequestDto dishRequestDto);

    @Operation(summary = "Update dish", description = "Updates the price and description of an existing dish. The authenticated user must be the owner of the restaurant to which the dish belongs.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid fields", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"price\",\"message\":\"Price must be a positive number\"}]}"))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user is not the owner of the restaurant", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"ownerId\",\"message\":\"You are not authorized to modify dishes for this restaurant\"}]}"))),
            @ApiResponse(responseCode = "404", description = "Dish not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"dishId\",\"message\":\"The dish does not exist\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<DishResponseDto> updateDish(Long id, DishUpdateRequestDto dishUpdateRequestDto);

    @Operation(summary = "List restaurant menu", description = "Returns a paginated list of active dishes for a restaurant, optionally filtered by category. Requires CUSTOMER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dishes retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedResponseDto.class),
                            examples = @ExampleObject(value = "{\"content\":[{\"id\":1,\"name\":\"Bandeja Paisa\",\"categoryId\":1,\"description\":\"Traditional Colombian dish\",\"price\":25000,\"urlImage\":\"https://example.com/bandeja.jpg\"},{\"id\":2,\"name\":\"Ajiaco\",\"categoryId\":1,\"description\":\"Traditional Colombian soup\",\"price\":18000,\"urlImage\":\"https://example.com/ajiaco.jpg\"}],\"totalElements\":20,\"totalPages\":2,\"currentPage\":0,\"pageSize\":10}"))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Invalid parameter type", value = "{\"message\":\"Invalid request parameter\",\"errors\":[{\"field\":\"page\",\"message\":\"Invalid value 'abc', expected type int\"}]}"),
                                    @ExampleObject(name = "Negative page or zero pageSize", value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"page\",\"message\":\"Page number must be zero or positive\"}]}")
                            })),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have CUSTOMER role",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Access Denied\"}"))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"restaurantId\",\"message\":\"The restaurant does not exist\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<PagedResponseDto<DishMenuResponseDto>> listDishes(
            @Parameter(description = "Restaurant ID", example = "1") Long restaurantId,
            @Parameter(description = "Category ID to filter by (optional)", example = "1") Long categoryId,
            @Parameter(description = "Page number (0-based)", example = "0") int page,
            @Parameter(description = "Number of elements per page", example = "10") int pageSize);
}
