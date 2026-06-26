package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Order;
import com.football.community.entity.Product;
import com.football.community.exception.BusinessException;
import com.football.community.repository.OrderMapper;
import com.football.community.service.OrderService;
import com.football.community.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public Order createOrder(Long productId, Integer quantity, Long userId) {
        Product product = productService.getById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        if (product.getStock() < quantity) {
            throw new BusinessException("库存不足");
        }

        product.setStock(product.getStock() - quantity);
        product.setSalesCount(product.getSalesCount() + quantity);
        productService.updateById(product);

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        order.setStatus(0);
        order.setCreatedAt(LocalDateTime.now());
        save(order);

        return order;
    }

    @Override
    public IPage<Order> getOrdersByUserId(Long userId, int page, int size) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Order getOrderById(Long id, Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getId, id)
               .eq(Order::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    @Transactional
    public void payOrder(Long id, Long userId) {
        Order order = getOrderById(id, userId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态异常");
        }

        order.setStatus(1);
        order.setPaymentTime(LocalDateTime.now());
        updateById(order);
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
