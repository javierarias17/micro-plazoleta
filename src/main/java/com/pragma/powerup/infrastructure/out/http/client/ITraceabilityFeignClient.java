package com.pragma.powerup.infrastructure.out.http.client;

import com.pragma.powerup.infrastructure.out.http.dto.OrderLogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "micro-traceability", url = "${adapter.micro-traceability.url}")
public interface ITraceabilityFeignClient {

    @PostMapping("/api/v1/order-logs")
    void saveOrderLog(@RequestBody OrderLogRequest request);
}
