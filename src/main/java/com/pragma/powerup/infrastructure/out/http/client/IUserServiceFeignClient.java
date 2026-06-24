package com.pragma.powerup.infrastructure.out.http.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "micro-users", url = "${adapter.micro-users.url}")
public interface IUserServiceFeignClient {

    @GetMapping("/api/v1/user/{userId}/is-owner")
    Boolean isOwner(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/user/employee/{employeeId}/restaurant-id")
    Long findRestaurantIdByEmployee(@PathVariable("employeeId") Long employeeId);
}
