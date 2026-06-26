package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchGroup;

import java.util.List;
import java.util.Map;

public interface MatchGroupService extends IService<MatchGroup> {

    void initGroups(Long matchId, Map<String, List<Long>> groupTeamMap);

    List<MatchGroup> getGroupsByMatchId(Long matchId);

    Map<String, List<MatchGroup>> getGroupStandings(Long matchId);

    void updateGroupResult(Long matchId, String groupName, Long homeTeamId, Long awayTeamId, int homeScore, int awayScore);

    List<Long> getTopTeamsFromGroup(Long matchId, String groupName, int topN);
}
