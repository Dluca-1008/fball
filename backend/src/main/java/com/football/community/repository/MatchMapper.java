package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.Match;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MatchMapper extends BaseMapper<Match> {
}
