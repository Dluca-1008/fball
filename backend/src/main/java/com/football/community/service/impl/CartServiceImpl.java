package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Cart;
import com.football.community.exception.BusinessException;
import com.football.community.repository.CartMapper;
import com.football.community.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Override
    public List<Cart> getCartByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .orderByDesc(Cart::getCreatedAt);
        return list(wrapper);
    }

    @Override
    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
               .eq(Cart::getProductId, productId);
        Cart existing = getOne(wrapper);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUpdatedAt(LocalDateTime.now());
            updateById(existing);
            return existing;
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            save(cart);
            return cart;
        }
    }

    @Override
    public void updateQuantity(Long id, Long userId, Integer quantity) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        cart.setQuantity(quantity);
        cart.setUpdatedAt(LocalDateTime.now());
        updateById(cart);
    }

    @Override
    public void removeFromCart(Long id, Long userId) {
        Cart cart = getById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        removeById(id);
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        remove(wrapper);
    }
}
