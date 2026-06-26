package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
