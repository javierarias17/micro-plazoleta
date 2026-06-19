package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantSummaryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "Check restaurant ownership", description = "Returns true if the authenticated user owns the restaurant. Used internally by micro-usuarios to validate ownership before linking an employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ownership check result", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "true"))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<Boolean> isOwner(@Parameter(description = "Restaurant ID", example = "1") Long restaurantId);

    @Operation(summary = "List restaurants", description = "Returns a paginated list of restaurants sorted alphabetically by name. Requires CUSTOMER role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PagedResponseDto.class),
                            examples = @ExampleObject(value = "{\"content\":[{\"id\":1,\"name\":\"El Corral\",\"urlLogo\":\"https://example.com/logo.png\"},{\"id\":2,\"name\":\"La Brasa\",\"urlLogo\":\"https://example.com/logo2.png\"}],\"totalElements\":42,\"totalPages\":5,\"currentPage\":0,\"pageSize\":10}"))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Invalid parameter type", value = "{\"message\":\"Invalid request parameter\",\"errors\":[{\"field\":\"page\",\"message\":\"Invalid value 'abc', expected type int\"}]}"),
                                    @ExampleObject(name = "Negative page or zero pageSize", value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"page\",\"message\":\"Page number must be zero or positive\"},{\"field\":\"pageSize\",\"message\":\"Page size must be a positive number\"}]}")
                            })),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have CUSTOMER role",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Access Denied\"}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<PagedResponseDto<RestaurantSummaryResponseDto>> listRestaurants(
            @Parameter(description = "Page number (0-based)", example = "0") int page,
            @Parameter(description = "Number of elements per page", example = "10") int pageSize);
}
