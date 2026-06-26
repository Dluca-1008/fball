package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Order;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<Order> createOrder(@RequestParam Long productId,
                                     @RequestParam(defaultValue = "1") Integer quantity,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(orderService.createOrder(productId, quantity, userDetails.getId()));
    }

    @GetMapping
    public Result<IPage<Order>> getMyOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(orderService.getOrdersByUserId(userDetails.getId(), page, size));
    }

    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(orderService.getOrderById(id, userDetails.getId()));
    }

    @PostMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        orderService.payOrder(id, userDetails.getId());
        return Result.success();
    }
}
