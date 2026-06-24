package com.pragma.powerup.infrastructure.out.http.client;

import com.pragma.powerup.infrastructure.out.http.dto.SmsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "micro-messaging", url = "${adapter.micro-messaging.url}")
public interface IMessagingFeignClient {

    @PostMapping("/api/v1/sms")
    void sendSms(@RequestBody SmsRequest request);
}
