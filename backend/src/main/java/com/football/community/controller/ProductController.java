package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Product;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<IPage<Product>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(productService.getProductList(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('product:add')")
    public Result<Product> createProduct(@RequestBody Product product,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        product.setMerchantId(userDetails.getId());
        return Result.success(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return Result.success(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public Result<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }
}
