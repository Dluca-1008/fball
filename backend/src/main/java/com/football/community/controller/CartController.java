package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Cart;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public Result<List<Cart>> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(cartService.getCartByUserId(userDetails.getId()));
    }

    @PostMapping
    public Result<Cart> addToCart(@RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") Integer quantity,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(cartService.addToCart(userDetails.getId(), productId, quantity));
    }

    @PutMapping("/{id}")
    public Result<?> updateQuantity(@PathVariable Long id,
                                    @RequestParam Integer quantity,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.updateQuantity(id, userDetails.getId(), quantity);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> removeFromCart(@PathVariable Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.removeFromCart(id, userDetails.getId());
        return Result.success();
    }

    @DeleteMapping("/clear")
    public Result<?> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getId());
        return Result.success();
    }
}
