package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Cart;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "购物车管理", description = "购物车CRUD接口")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "获取购物车", description = "获取当前用户的购物车商品列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result<List<Cart>> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(cartService.getCartByUserId(userDetails.getId()));
    }

    @Operation(summary = "添加商品到购物车", description = "将指定商品添加到购物车")
    @Parameters({
            @Parameter(name = "productId", description = "商品ID", required = true),
            @Parameter(name = "quantity", description = "数量", example = "1")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public Result<Cart> addToCart(@RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") Integer quantity,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(cartService.addToCart(userDetails.getId(), productId, quantity));
    }

    @Operation(summary = "更新购物车数量", description = "更新购物车中指定商品的数量")
    @Parameters({
            @Parameter(name = "id", description = "购物车项ID", required = true),
            @Parameter(name = "quantity", description = "新数量", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/{id}")
    public Result<?> updateQuantity(@PathVariable Long id,
                                    @RequestParam Integer quantity,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.updateQuantity(id, userDetails.getId(), quantity);
        return Result.success();
    }

    @Operation(summary = "删除购物车商品", description = "从购物车中移除指定商品")
    @Parameter(name = "id", description = "购物车项ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    public Result<?> removeFromCart(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeFromCart(id, userDetails.getId());
        return Result.success();
    }

    @Operation(summary = "清空购物车", description = "清空当前用户购物车中的所有商品")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "清空成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/clear")
    public Result<?> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getId());
        return Result.success();
    }
}
