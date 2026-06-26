package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Product;
import com.football.community.exception.BusinessException;
import com.football.community.repository.ProductMapper;
import com.football.community.service.ProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<Product> getProductList(int page, int size, String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Product::getName, keyword)
                   .or().like(Product::getDescription, keyword);
        }
        wrapper.orderByDesc(Product::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public Product createProduct(Product product) {
        product.setSalesCount(0);
        product.setStatus(1);
        product.setCreatedAt(LocalDateTime.now());
        save(product);
        return product;
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        Product existing = getById(id);
        if (existing == null) {
            throw new BusinessException("商品不存在");
        }
        product.setId(id);
        updateById(product);
        return getById(id);
    }

    @Override
    public void deleteProduct(Long id) {
        removeById(id);
    }
}
