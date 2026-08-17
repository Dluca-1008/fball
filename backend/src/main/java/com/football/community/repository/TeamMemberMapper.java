package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.TeamMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    @Select("SELECT COUNT(*) > 0 FROM team_members WHERE team_id = #{teamId} AND user_id = #{userId} AND role = 'admin' AND status = 1")
    boolean isTeamAdmin(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) > 0 FROM team_members WHERE team_id = #{teamId} AND user_id = #{userId} AND status = 1")
    boolean isTeamMember(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT team_id FROM team_members WHERE user_id = #{userId} AND status = 1 LIMIT 1")
    Long getTeamIdByUserId(@Param("userId") Long userId);
}
