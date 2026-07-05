package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Product;

/**
 * 商品服务接口。
 * <p>提供商品CRUD及分页搜索等业务逻辑。</p>
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页获取商品列表。
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 商品分页结果
     */
    IPage<Product> getProductList(int page, int size, String keyword);

    /**
     * 创建新商品。
     * @param product 商品实体
     * @return 创建后的商品
     */
    Product createProduct(Product product);

    /**
     * 更新商品信息。
     * @param id 商品ID
     * @param product 商品实体
     * @return 更新后的商品
     */
    Product updateProduct(Long id, Product product);

    /**
     * 删除商品。
     * @param id 商品ID
     */
    void deleteProduct(Long id);
}
