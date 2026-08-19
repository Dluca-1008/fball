package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CoachMapper extends BaseMapper<Coach> {

    @Select("SELECT COUNT(1) FROM coaches WHERE user_id = #{userId}")
    int existsByUserId(@Param("userId") Long userId);
}
