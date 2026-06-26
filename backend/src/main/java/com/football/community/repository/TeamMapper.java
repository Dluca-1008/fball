package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.Team;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamMapper extends BaseMapper<Team> {
}
