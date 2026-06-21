package com.pragma.powerup.infrastructure.input.rest;

import com.pragma.powerup.application.dto.request.OrderRequestDto;
import com.pragma.powerup.application.dto.response.OrderResponseDto;
import com.pragma.powerup.application.dto.response.PagedResponseDto;
import com.pragma.powerup.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface IOrderRestControllerDocs {

    @Operation(summary = "Place an order", description = "Allows a customer to place an order with dishes from a single restaurant. The customer must not have any active order (PENDING, IN_PREPARATION or READY).")
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

    @Operation(summary = "List orders by status", description = "Returns a paginated list of orders filtered by status. Only orders belonging to the restaurant where the authenticated employee works are returned.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters or missing/invalid status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "Missing status", value = "{\"message\":\"Invalid request parameter\",\"errors\":[{\"field\":\"status\",\"message\":\"Required parameter 'status' is missing\"}]}"),
                                    @ExampleObject(name = "Invalid status value", value = "{\"message\":\"Invalid request parameter\",\"errors\":[{\"field\":\"status\",\"message\":\"Invalid value 'INVALID', expected type OrderStatus\"}]}"),
                                    @ExampleObject(name = "Invalid pagination", value = "{\"message\":\"Validation failed\",\"errors\":[{\"field\":\"page\",\"message\":\"Page number must be zero or positive\"}]}")
                            })),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have EMPLOYEE role or is not linked to any restaurant",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "No role", value = "{\"message\":\"Access Denied\"}"),
                                    @ExampleObject(name = "Not linked", value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"employeeId\",\"message\":\"The employee is not linked to any restaurant\"}]}")
                            })),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<PagedResponseDto<OrderResponseDto>> listOrders(
            @Parameter(description = "Order status filter", required = true,
                    schema = @Schema(implementation = OrderStatus.class)) OrderStatus status,
            @Parameter(description = "Page number (0-based)", example = "0") int page,
            @Parameter(description = "Number of elements per page", example = "10") int pageSize);

    @Operation(summary = "Assign order to employee", description = "Allows an employee to assign themselves to a PENDING order and change its status to IN_PREPARATION. The order must belong to the restaurant where the employee works.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order assigned successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Order is not in PENDING status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"orderId\",\"message\":\"The order cannot be assigned because it is not in PENDING status\"}]}"))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have EMPLOYEE role, is not linked to any restaurant, or order does not belong to employee's restaurant",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "No role", value = "{\"message\":\"Access Denied\"}"),
                                    @ExampleObject(name = "Not linked", value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"employeeId\",\"message\":\"The employee is not linked to any restaurant\"}]}"),
                                    @ExampleObject(name = "Order not from restaurant", value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"orderId\",\"message\":\"The order does not belong to your restaurant\"}]}")
                            })),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"orderId\",\"message\":\"The order does not exist\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<OrderResponseDto> assignOrder(
            @Parameter(description = "ID of the order to assign", required = true) Long orderId);

    @Operation(summary = "Mark order as ready", description = "Marks an IN_PREPARATION order as READY, generates a security PIN and sends it via SMS to the customer's phone. Only the employee assigned to the order's restaurant can perform this action.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order marked as ready and customer notified",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"No authentication token provided.\"}"))),
            @ApiResponse(responseCode = "403", description = "Employee not linked to the order's restaurant",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Access Denied\"}"))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"orderId\",\"message\":\"The order does not exist\"}]}"))),
            @ApiResponse(responseCode = "409", description = "Order is not in IN_PREPARATION status",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"Business validation failed\",\"errors\":[{\"field\":\"orderId\",\"message\":\"The order cannot be marked as ready because it is not in IN_PREPARATION status\"}]}"))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = "{\"message\":\"An unexpected error occurred. Please contact the administrator.\"}")))
    })
    ResponseEntity<OrderResponseDto> notifyOrderReady(
            @Parameter(description = "ID of the order to mark as ready", required = true) Long orderId);
}
