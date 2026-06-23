package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.constant.TechnicalMessageConstants;
import com.pragma.powerup.domain.spi.INotifyClientPort;
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

import java.util.Map;

@RequiredArgsConstructor
public class MessagingAdapter implements INotifyClientPort {

    private static final String ENDPOINT_SEND_SMS = "/api/v1/sms";
    private static final String FIELD_CUSTOMER_ID = "customerId";
    private static final String FIELD_PIN = "pin";

    private final RestTemplate restTemplate;
    private final String messagingServiceUrl;

    @Override
    public void notify(Long customerId, String pin) {
        try {
            String url = messagingServiceUrl + ENDPOINT_SEND_SMS;
            HttpHeaders headers = buildAuthHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                    Map.of(FIELD_CUSTOMER_ID, customerId, FIELD_PIN, pin), headers);
            restTemplate.postForEntity(url, request, Void.class);
        } catch (HttpServerErrorException e) {
            throw new TechnicalException(e.getMessage());
        }
        catch (ResourceAccessException e) {
            throw new TechnicalException(TechnicalMessageConstants.MESSAGING_UNAVAILABLE);
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
