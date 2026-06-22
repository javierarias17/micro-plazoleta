package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderModel;

public interface IDeliverOrderServicePort {
    OrderModel deliverOrder(Long orderId, String securityPin);
}
