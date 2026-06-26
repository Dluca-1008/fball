package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.football.community.entity.Match;
import com.football.community.entity.MatchGroup;
import com.football.community.entity.Team;
import com.football.community.repository.MatchGroupMapper;
import com.football.community.repository.MatchMapper;
import com.football.community.service.MatchStatsService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchStatsServiceImpl implements MatchStatsService {

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private MatchGroupMapper matchGroupMapper;

    @Autowired
    private TeamService teamService;

    @Override
    public Map<String, Object> getLeagueStats(Long matchId) {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<Match> matchWrapper = new LambdaQueryWrapper<>();
        matchWrapper.eq(Match::getLeagueId, matchId);
        List<Match> matches = matchMapper.selectList(matchWrapper);

        int totalMatches = matches.size();
        int completedMatches = (int) matches.stream().filter(m -> m.getStatus() == 2).count();
        int totalGoals = matches.stream().mapToInt(m -> (m.getHomeScore() != null ? m.getHomeScore() : 0) + (m.getAwayScore() != null ? m.getAwayScore() : 0)).sum();
        double avgGoals = completedMatches > 0 ? (double) totalGoals / completedMatches : 0;

        stats.put("totalMatches", totalMatches);
        stats.put("completedMatches", completedMatches);
        stats.put("pendingMatches", totalMatches - completedMatches);
        stats.put("totalGoals", totalGoals);
        stats.put("avgGoalsPerMatch", Math.round(avgGoals * 100.0) / 100.0);

        LambdaQueryWrapper<MatchGroup> groupWrapper = new LambdaQueryWrapper<>();
        groupWrapper.eq(MatchGroup::getMatchId, matchId);
        List<MatchGroup> groups = matchGroupMapper.selectList(groupWrapper);

        Map<String, List<MatchGroup>> grouped = groups.stream()
                .collect(Collectors.groupingBy(MatchGroup::getGroupName));
        stats.put("groups", grouped.keySet());
        stats.put("groupCount", grouped.size());

        int totalTeams = (int) groups.stream().map(MatchGroup::getTeamId).distinct().count();
        stats.put("totalTeams", totalTeams);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getTeamStats(Long teamId) {
        List<Map<String, Object>> statsList = new ArrayList<>();

        LambdaQueryWrapper<Match> homeWrapper = new LambdaQueryWrapper<>();
        homeWrapper.eq(Match::getHomeTeamId, teamId);
        List<Match> homeMatches = matchMapper.selectList(homeWrapper);

        LambdaQueryWrapper<Match> awayWrapper = new LambdaQueryWrapper<>();
        awayWrapper.eq(Match::getAwayTeamId, teamId);
        List<Match> awayMatches = matchMapper.selectList(awayWrapper);

        List<Match> allMatches = new ArrayList<>();
        allMatches.addAll(homeMatches);
        allMatches.addAll(awayMatches);

        int totalMatches = allMatches.size();
        int wins = 0, draws = 0, losses = 0;
        int goalsFor = 0, goalsAgainst = 0;

        for (Match match : allMatches) {
            if (match.getStatus() != 2) continue;

            boolean isHome = match.getHomeTeamId().equals(teamId);
            int myScore = isHome ? match.getHomeScore() : match.getAwayScore();
            int oppScore = isHome ? match.getAwayScore() : match.getHomeScore();

            goalsFor += myScore;
            goalsAgainst += oppScore;

            if (myScore > oppScore) wins++;
            else if (myScore == oppScore) draws++;
            else losses++;
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("teamId", teamId);
        Team team = teamService.getById(teamId);
        summary.put("teamName", team != null ? team.getName() : "Unknown");
        summary.put("totalMatches", totalMatches);
        summary.put("wins", wins);
        summary.put("draws", draws);
        summary.put("losses", losses);
        summary.put("goalsFor", goalsFor);
        summary.put("goalsAgainst", goalsAgainst);
        summary.put("goalDifference", goalsFor - goalsAgainst);
        summary.put("points", wins * 3 + draws);
        summary.put("winRate", totalMatches > 0 ? Math.round((double) wins / totalMatches * 100) : 0);
        statsList.add(summary);

        return statsList;
    }

    @Override
    public Map<String, Object> getOverallStats() {
        Map<String, Object> stats = new HashMap<>();

        LambdaQueryWrapper<Match> allWrapper = new LambdaQueryWrapper<>();
        List<Match> allMatches = matchMapper.selectList(allWrapper);

        stats.put("totalMatches", allMatches.size());
        stats.put("completedMatches", allMatches.stream().filter(m -> m.getStatus() == 2).count());
        stats.put("inProgressMatches", allMatches.stream().filter(m -> m.getStatus() == 1).count());
        stats.put("pendingMatches", allMatches.stream().filter(m -> m.getStatus() == 0).count());

        int totalGoals = allMatches.stream()
                .filter(m -> m.getStatus() == 2)
                .mapToInt(m -> (m.getHomeScore() != null ? m.getHomeScore() : 0) + (m.getAwayScore() != null ? m.getAwayScore() : 0))
                .sum();
        long completedCount = allMatches.stream().filter(m -> m.getStatus() == 2).count();
        stats.put("totalGoals", totalGoals);
        stats.put("avgGoalsPerMatch", completedCount > 0 ? Math.round((double) totalGoals / completedCount * 100.0) / 100.0 : 0);

        stats.put("leagueMatches", allMatches.stream().filter(m -> "league".equals(m.getMatchType())).count());
        stats.put("cupMatches", allMatches.stream().filter(m -> "cup".equals(m.getMatchType())).count());

        Set<Long> teamIds = new HashSet<>();
        allMatches.forEach(m -> {
            if (m.getHomeTeamId() != null) teamIds.add(m.getHomeTeamId());
            if (m.getAwayTeamId() != null) teamIds.add(m.getAwayTeamId());
        });
        stats.put("totalTeams", teamIds.size());

        return stats;
    }

    @Override
    public List<Map<String, Object>> getTopScorers(int limit) {
        LambdaQueryWrapper<Match> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Match::getStatus, 2);
        List<Match> completedMatches = matchMapper.selectList(wrapper);

        Map<Long, Integer> teamGoals = new HashMap<>();
        for (Match match : completedMatches) {
            if (match.getHomeScore() != null) {
                teamGoals.merge(match.getHomeTeamId(), match.getHomeScore(), Integer::sum);
            }
            if (match.getAwayScore() != null) {
                teamGoals.merge(match.getAwayTeamId(), match.getAwayScore(), Integer::sum);
            }
        }

        return teamGoals.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    Team team = teamService.getById(entry.getKey());
                    item.put("teamId", entry.getKey());
                    item.put("teamName", team != null ? team.getName() : "Unknown");
                    item.put("goals", entry.getValue());
                    return item;
                })
                .toList();
    }
}
