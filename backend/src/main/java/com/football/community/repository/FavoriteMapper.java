package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
