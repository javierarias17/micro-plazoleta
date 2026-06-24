package com.pragma.powerup.infrastructure.out.http.adapter;

import com.pragma.powerup.domain.exception.TechnicalException;
import com.pragma.powerup.domain.exception.constant.TechnicalMessageConstants;
import com.pragma.powerup.domain.spi.INotifyClientPort;
import com.pragma.powerup.infrastructure.out.http.client.IMessagingFeignClient;
import com.pragma.powerup.infrastructure.out.http.dto.SmsRequest;
import feign.FeignException;
import feign.RetryableException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessagingAdapter implements INotifyClientPort {

    private final IMessagingFeignClient feignClient;

    @Override
    public void sendSms(Long customerId, String pin) {
        try {
            feignClient.sendSms(new SmsRequest(customerId, pin));
        } catch (RetryableException e) {
            throw new TechnicalException(TechnicalMessageConstants.MESSAGING_UNAVAILABLE);
        } catch (FeignException e) {
            throw new TechnicalException(e.getMessage());
        }
    }
}
