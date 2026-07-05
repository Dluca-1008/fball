package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Order;

/**
 * 订单服务接口。
 * <p>提供订单创建、查询、支付等业务逻辑。</p>
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建新订单。
     * @param productId 商品ID
     * @param quantity 购买数量
     * @param userId 用户ID
     * @return 创建的订单
     */
    Order createOrder(Long productId, Integer quantity, Long userId);

    /**
     * 分页获取用户的订单列表。
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页结果
     */
    IPage<Order> getOrdersByUserId(Long userId, int page, int size);

    /**
     * 获取订单详情。
     * @param id 订单ID
     * @param userId 用户ID（用于权限校验）
     * @return 订单实体
     */
    Order getOrderById(Long id, Long userId);

    /**
     * 支付订单。
     * @param id 订单ID
     * @param userId 用户ID
     */
    void payOrder(Long id, Long userId);
}
