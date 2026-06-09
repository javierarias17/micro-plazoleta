package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.TechnicalExceptionResponse;
import com.pragma.powerup.domain.spi.IUserValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class UserValidationAdapter implements IUserValidationPort {

    private final RestTemplate restTemplate;
    private final String usersServiceUrl;

    @Override
    public boolean isOwner(Long userId) {
        try {
            String url = usersServiceUrl + "/api/v1/user/" + userId + "/is-owner";
            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return Boolean.TRUE.equals(result);
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (ResourceAccessException e) {
            throw new TechnicalException(TechnicalExceptionResponse.USER_VALIDATION_UNAVAILABLE.getMessage());
        }
    }
}
