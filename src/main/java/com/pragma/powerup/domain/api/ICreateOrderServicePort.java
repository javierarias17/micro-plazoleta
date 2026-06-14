package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.OrderModel;

public interface ICreateOrderServicePort {
    OrderModel createOrder(OrderModel orderModel);
}
