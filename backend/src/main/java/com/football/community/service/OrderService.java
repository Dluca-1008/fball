package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Order;

public interface OrderService extends IService<Order> {

    Order createOrder(Long productId, Integer quantity, Long userId);

    IPage<Order> getOrdersByUserId(Long userId, int page, int size);

    Order getOrderById(Long id, Long userId);

    void payOrder(Long id, Long userId);
}
