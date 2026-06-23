package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.constant.TechnicalMessageConstants;
import com.pragma.powerup.domain.model.OrderStatus;
import com.pragma.powerup.domain.spi.ITraceabilityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TraceabilityAdapter implements ITraceabilityPort {

    private static final String ENDPOINT_SAVE_LOG = "/api/v1/order-logs";
    private static final String FIELD_ORDER_ID = "orderId";
    private static final String FIELD_RESTAURANT_ID = "restaurantId";
    private static final String FIELD_CUSTOMER_ID = "customerId";
    private static final String FIELD_EMPLOYEE_ID = "employeeId";
    private static final String FIELD_PREVIOUS_STATUS = "previousStatus";
    private static final String FIELD_NEW_STATUS = "newStatus";
    private static final String FIELD_DATE = "date";

    private final RestTemplate restTemplate;
    private final String traceabilityServiceUrl;

    @Override
    public void logStatusChange(Long orderId, Long restaurantId, Long customerId, Long employeeId,
                                OrderStatus previousStatus, OrderStatus newStatus,
                                LocalDateTime occurredAt) {
        try {
            String url = traceabilityServiceUrl + ENDPOINT_SAVE_LOG;
            HttpHeaders headers = buildAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put(FIELD_ORDER_ID, orderId);
            body.put(FIELD_RESTAURANT_ID, restaurantId);
            body.put(FIELD_CUSTOMER_ID, customerId);
            body.put(FIELD_EMPLOYEE_ID, employeeId);
            body.put(FIELD_PREVIOUS_STATUS, previousStatus != null ? previousStatus.name() : null);
            body.put(FIELD_NEW_STATUS, newStatus.name());
            body.put(FIELD_DATE, occurredAt);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, request, Void.class);
        } catch (HttpServerErrorException e) {
            throw new TechnicalException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new TechnicalException(TechnicalMessageConstants.TRACEABILITY_UNAVAILABLE);
        }
    }

    @NonNull
    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null) {
                headers.set(HttpHeaders.AUTHORIZATION, authHeader);
            }
        }
        return headers;
    }
}
