package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Order;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单管理", description = "订单相关接口")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单", description = "根据商品ID和数量创建新订单")
    @Parameters({
            @Parameter(name = "productId", description = "商品ID", required = true),
            @Parameter(name = "quantity", description = "购买数量", example = "1")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public Result<Order> createOrder(@RequestParam Long productId,
                                     @RequestParam(defaultValue = "1") Integer quantity,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(orderService.createOrder(productId, quantity, userDetails.getId()));
    }

    @Operation(summary = "获取我的订单", description = "分页获取当前用户的订单列表")
    @Parameters({
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Order>> getMyOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(orderService.getOrdersByUserId(userDetails.getId(), page, size));
    }

    @Operation(summary = "获取订单详情", description = "根据ID获取订单详细信息")
    @Parameter(name = "id", description = "订单ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "404", description = "订单不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public Result<Order> getOrder(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(orderService.getOrderById(id, userDetails.getId()));
    }

    @Operation(summary = "支付订单", description = "支付指定订单")
    @Parameter(name = "id", description = "订单ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "支付成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        orderService.payOrder(id, userDetails.getId());
        return Result.success();
    }
}
