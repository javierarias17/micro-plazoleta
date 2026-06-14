package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface IOrderRestControllerDocs {

    @Operation(summary = "Place an order", description = "Allows a customer to place an order with dishes from a single restaurant. The customer must not have any active order (PENDIENTE, EN_PREPARACION or LISTO).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid fields or dishes not available for the restaurant",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Missing dishes", value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"dishes\",\"message\":\"The order must contain at least one dish\"}]}"),
                                    @ExampleObject(name = "Dish not from restaurant", value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"dishes\",\"message\":\"One or more dishes are not available, do not belong to the specified restaurant, or do not exist\"}]}")
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
            @ApiResponse(responseCode = "409", description = "Customer already has an active order",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"clientId\",\"message\":\"You already have an order in process. You cannot place a new order until it is completed or cancelled\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<OrderResponseDto> createOrder(OrderRequestDto orderRequestDto);
}
