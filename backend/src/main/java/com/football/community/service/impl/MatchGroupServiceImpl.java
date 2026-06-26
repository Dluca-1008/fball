package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.MatchGroup;
import com.football.community.entity.Team;
import com.football.community.exception.BusinessException;
import com.football.community.repository.MatchGroupMapper;
import com.football.community.service.MatchGroupService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchGroupServiceImpl extends ServiceImpl<MatchGroupMapper, MatchGroup> implements MatchGroupService {

    @Autowired
    private TeamService teamService;

    @Override
    @Transactional
    public void initGroups(Long matchId, Map<String, List<Long>> groupTeamMap) {
        LambdaQueryWrapper<MatchGroup> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(MatchGroup::getMatchId, matchId);
        remove(deleteWrapper);

        for (Map.Entry<String, List<Long>> entry : groupTeamMap.entrySet()) {
            String groupName = entry.getKey();
            List<Long> teamIds = entry.getValue();
            for (Long teamId : teamIds) {
                MatchGroup group = new MatchGroup();
                group.setMatchId(matchId);
                group.setGroupName(groupName);
                group.setTeamId(teamId);
                group.setPlayed(0);
                group.setWon(0);
                group.setDrawn(0);
                group.setLost(0);
                group.setGoalsFor(0);
                group.setGoalsAgainst(0);
                group.setPoints(0);
                group.setCreatedAt(LocalDateTime.now());
                save(group);
            }
        }
    }

    @Override
    public List<MatchGroup> getGroupsByMatchId(Long matchId) {
        LambdaQueryWrapper<MatchGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchGroup::getMatchId, matchId);
        List<MatchGroup> groups = list(wrapper);
        groups.forEach(g -> {
            g.setGoalDifference(g.getGoalsFor() - g.getGoalsAgainst());
            Team team = teamService.getById(g.getTeamId());
            if (team != null) {
                g.setTeamName(team.getName());
            }
        });
        groups.sort(Comparator.comparing(MatchGroup::getGroupName)
                .thenComparing((g) -> -g.getPoints())
                .thenComparing((g) -> -g.getGoalDifference())
                .thenComparing((g) -> -g.getGoalsFor()));
        return groups;
    }

    @Override
    public Map<String, List<MatchGroup>> getGroupStandings(Long matchId) {
        List<MatchGroup> allGroups = getGroupsByMatchId(matchId);
        return allGroups.stream()
                .collect(Collectors.groupingBy(MatchGroup::getGroupName, LinkedHashMap::new, Collectors.toList()));
    }

    @Override
    @Transactional
    public void updateGroupResult(Long matchId, String groupName, Long homeTeamId, Long awayTeamId, int homeScore, int awayScore) {
        updateTeamStats(matchId, groupName, homeTeamId, homeScore, awayScore, homeScore > awayScore);
        updateTeamStats(matchId, groupName, awayTeamId, awayScore, homeScore, awayScore > homeScore);
    }

    private void updateTeamStats(Long matchId, String groupName, Long teamId, int goalsFor, int goalsAgainst, boolean won) {
        LambdaQueryWrapper<MatchGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchGroup::getMatchId, matchId)
               .eq(MatchGroup::getGroupName, groupName)
               .eq(MatchGroup::getTeamId, teamId);
        MatchGroup group = getOne(wrapper);
        if (group == null) return;

        group.setPlayed(group.getPlayed() + 1);
        group.setGoalsFor(group.getGoalsFor() + goalsFor);
        group.setGoalsAgainst(group.getGoalsAgainst() + goalsAgainst);

        if (goalsFor == goalsAgainst) {
            group.setDrawn(group.getDrawn() + 1);
            group.setPoints(group.getPoints() + 1);
        } else if (won) {
            group.setWon(group.getWon() + 1);
            group.setPoints(group.getPoints() + 3);
        } else {
            group.setLost(group.getLost() + 1);
        }
        updateById(group);
    }

    @Override
    public List<Long> getTopTeamsFromGroup(Long matchId, String groupName, int topN) {
        LambdaQueryWrapper<MatchGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchGroup::getMatchId, matchId)
               .eq(MatchGroup::getGroupName, groupName);
        List<MatchGroup> groups = list(wrapper);
        groups.forEach(g -> g.setGoalDifference(g.getGoalsFor() - g.getGoalsAgainst()));
        groups.sort(Comparator.comparing(MatchGroup::getPoints).reversed()
                .thenComparing(Comparator.comparing(MatchGroup::getGoalDifference).reversed())
                .thenComparing(Comparator.comparing(MatchGroup::getGoalsFor).reversed()));
        return groups.stream()
                .limit(topN)
                .map(MatchGroup::getTeamId)
                .toList();
    }
}
