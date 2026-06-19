package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.constant.TechnicalMessageConstants;
import com.pragma.powerup.domain.spi.IUserServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceAdapter implements IUserServicePort {

    private static final String ENDPOINT_USER_IS_OWNER = "/api/v1/user/%s/is-owner";
    private static final String ENDPOINT_EMPLOYEE_RESTAURANT_ID = "/api/v1/user/employee/%s/restaurant-id";

    private final RestTemplate restTemplate;
    private final String usersServiceUrl;

    @Override
    public boolean isOwner(Long userId) {
        try {
            String url = usersServiceUrl + String.format(ENDPOINT_USER_IS_OWNER, userId);
            HttpEntity<Void> request = new HttpEntity<>(buildAuthHeaders());
            Boolean result = restTemplate.exchange(url, HttpMethod.GET, request, Boolean.class).getBody();
            return Boolean.TRUE.equals(result);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new TechnicalException(TechnicalMessageConstants.USER_VALIDATION_UNAVAILABLE);
        }
    }

    @Override
    public Optional<Long> findRestaurantIdByEmployeeId(Long employeeId) {
        try {
            String url = usersServiceUrl + String.format(ENDPOINT_EMPLOYEE_RESTAURANT_ID, employeeId);
            HttpEntity<Void> request = new HttpEntity<>(buildAuthHeaders());
            Long restaurantId = restTemplate.exchange(url, HttpMethod.GET, request, Long.class).getBody();
            return Optional.ofNullable(restaurantId);
        } catch (ResourceAccessException e) {
            throw new TechnicalException(TechnicalMessageConstants.USER_VALIDATION_UNAVAILABLE);
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
