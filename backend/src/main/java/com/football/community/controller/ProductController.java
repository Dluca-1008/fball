package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Product;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理", description = "商品CRUD接口")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(summary = "获取商品列表", description = "分页获取商品列表，支持关键词搜索")
    @Parameters({
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10"),
            @Parameter(name = "keyword", description = "搜索关键词")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Product>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return Result.success(productService.getProductList(page, size, keyword));
    }

    @Operation(summary = "获取商品详情", description = "根据ID获取商品详细信息")
    @Parameter(name = "id", description = "商品ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "商品不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return Result.success(productService.getById(id));
    }

    @Operation(summary = "创建商品", description = "新增一件商品")
    @Parameter(description = "商品信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('product:add')")
    public Result<Product> createProduct(@RequestBody Product product,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        product.setMerchantId(userDetails.getId());
        return Result.success(productService.createProduct(product));
    }

    @Operation(summary = "更新商品信息", description = "修改指定商品的信息")
    @Parameters({
            @Parameter(name = "id", description = "商品ID", required = true),
            @Parameter(description = "商品信息")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('product:edit')")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return Result.success(productService.updateProduct(id, product));
    }

    @Operation(summary = "删除商品", description = "根据ID删除指定商品")
    @Parameter(name = "id", description = "商品ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('product:delete')")
    public Result<?> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }
}
