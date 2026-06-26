package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Team;

import java.util.Map;

public interface TeamService extends IService<Team> {

    IPage<Team> getTeamList(int page, int size, String keyword);

    Team createTeam(Team team, Long userId, String memberType, Map<String, Object> memberInfo);

    Team updateTeam(Long id, Team team);

    void deleteTeam(Long id);

    boolean isTeamAdmin(Long teamId, Long userId);

    boolean isTeamMember(Long teamId, Long userId);

    void dissolveTeam(Long teamId, Long userId);
}
