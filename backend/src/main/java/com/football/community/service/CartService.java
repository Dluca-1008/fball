package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    List<Cart> getCartByUserId(Long userId);

    Cart addToCart(Long userId, Long productId, Integer quantity);

    void updateQuantity(Long id, Long userId, Integer quantity);

    void removeFromCart(Long id, Long userId);

    void clearCart(Long userId);
}
