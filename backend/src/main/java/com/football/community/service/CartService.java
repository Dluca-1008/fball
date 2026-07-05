package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Cart;

import java.util.List;

/**
 * 购物车服务接口。
 * <p>提供购物车增删改查等业务逻辑。</p>
 */
public interface CartService extends IService<Cart> {

    /**
     * 获取指定用户的购物车商品列表。
     * @param userId 用户ID
     * @return 购物车商品列表
     */
    List<Cart> getCartByUserId(Long userId);

    /**
     * 将商品添加到购物车。
     * @param userId 用户ID
     * @param productId 商品ID
     * @param quantity 数量
     * @return 添加后的购物车项
     */
    Cart addToCart(Long userId, Long productId, Integer quantity);

    /**
     * 更新购物车项数量。
     * @param id 购物车项ID
     * @param userId 用户ID
     * @param quantity 新数量
     */
    void updateQuantity(Long id, Long userId, Integer quantity);

    /**
     * 从购物车中移除指定商品。
     * @param id 购物车项ID
     * @param userId 用户ID
     */
    void removeFromCart(Long id, Long userId);

    /**
     * 清空指定用户的购物车。
     * @param userId 用户ID
     */
    void clearCart(Long userId);
}
