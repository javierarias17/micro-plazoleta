package com.pragma.powerup.infrastructure.out.http.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SmsRequest {

    private Long customerId;
    private String pin;
}
