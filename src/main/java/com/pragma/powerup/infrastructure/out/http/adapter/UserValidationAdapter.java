package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.TechnicalExceptionResponse;
import com.pragma.powerup.domain.spi.IUserValidationPort;
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

@RequiredArgsConstructor
public class UserValidationAdapter implements IUserValidationPort {

    private final RestTemplate restTemplate;
    private final String usersServiceUrl;

    @Override
    public boolean isOwner(Long userId) {
        try {
            String url = usersServiceUrl + "/api/v1/user/" + userId + "/is-owner";
            HttpEntity<Void> request = new HttpEntity<>(buildAuthHeaders());
            Boolean result = restTemplate.exchange(url, HttpMethod.GET, request, Boolean.class).getBody();
            return Boolean.TRUE.equals(result);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new TechnicalException(TechnicalExceptionResponse.USER_VALIDATION_UNAVAILABLE.getMessage());
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
