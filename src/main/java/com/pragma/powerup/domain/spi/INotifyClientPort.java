package com.pragma.powerup.domain.spi;

public interface INotifyClientPort {
    void sendSms(Long clientId, String pin);
}
