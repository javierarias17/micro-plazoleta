package com.pragma.powerup.domain.spi;

public interface INotifyClientPort {
    void notify(Long clientId, String pin);
}
