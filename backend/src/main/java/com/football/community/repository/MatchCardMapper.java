package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.MatchCard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MatchCardMapper extends BaseMapper<MatchCard> {

    @Select("SELECT t.name AS team_name, u.nickname AS player_name, c.card_type, c.minute, c.match_id, c.team_id, c.player_id FROM match_cards c " +
            "LEFT JOIN teams t ON c.team_id = t.id " +
            "LEFT JOIN users u ON c.player_id = u.id " +
            "WHERE c.match_id = #{matchId} ORDER BY c.minute ASC, c.created_at ASC")
    List<MatchCard> selectWithPlayerInfo(@Param("matchId") Long matchId);
}
