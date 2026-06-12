package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.EmployeeLinkRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface IRestaurantRestControllerDocs {

    @Operation(summary = "Create restaurant", description = "Creates a new restaurant. Requires ADMIN role. The ownerId must correspond to a user with the OWNER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RestaurantResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid fields", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"name\",\"message\":\"Name cannot consist of numbers only\"},{\"field\":\"nit\",\"message\":\"NIT must contain only digits\"}]}"))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have ADMIN role", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Access Denied\"}"))),
            @ApiResponse(responseCode = "404", description = "Owner user not found or does not have OWNER role", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"ownerId\",\"message\":\"The provided user does not exist or does not have the owner role\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<RestaurantResponseDto> createRestaurant(RestaurantRequestDto restaurantRequestDto);

    @Operation(summary = "Link employee to restaurant", description = "Links an existing employee user to a restaurant. Requires OWNER role. The authenticated owner must own the restaurant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee linked successfully"),
            @ApiResponse(responseCode = "400", description = "Required fields are null", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"employeeId\",\"message\":\"Employee ID is required\"}]}"))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have OWNER role or does not own the restaurant", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Access Denied\"}"))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found or the user does not have the EMPLOYEE role", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = {
                    @ExampleObject(name = "Restaurant not found", value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"restaurantId\",\"message\":\"The restaurant does not exist\"}]}"),
                    @ExampleObject(name = "User is not an employee", value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"employeeId\",\"message\":\"The provided user does not exist or does not have the owner role\"}]}")
            })),
            @ApiResponse(responseCode = "409", description = "Employee is already linked to a restaurant", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"employeeId\",\"message\":\"This employee is already linked to a restaurant\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error or failure communicating with micro-usuarios", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<Void> linkEmployee(Long restaurantId, EmployeeLinkRequestDto employeeLinkRequestDto);
}
