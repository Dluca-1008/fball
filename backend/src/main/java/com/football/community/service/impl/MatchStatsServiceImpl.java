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
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

        Long totalMatches = matchMapper.selectCount(new LambdaQueryWrapper<>());
        Long completedMatches = matchMapper.selectCount(
                new LambdaQueryWrapper<Match>().eq(Match::getStatus, 2));
        Long inProgressMatches = matchMapper.selectCount(
                new LambdaQueryWrapper<Match>().eq(Match::getStatus, 1));
        Long pendingMatches = matchMapper.selectCount(
                new LambdaQueryWrapper<Match>().eq(Match::getStatus, 0));

        stats.put("totalMatches", totalMatches);
        stats.put("completedMatches", completedMatches);
        stats.put("inProgressMatches", inProgressMatches);
        stats.put("pendingMatches", pendingMatches);

        Integer totalGoals = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(home_score + away_score), 0) FROM matches WHERE status = 2",
                Integer.class);
        long completedCount = completedMatches != null ? completedMatches : 0;
        int totalGoalsVal = totalGoals != null ? totalGoals : 0;
        stats.put("totalGoals", totalGoalsVal);
        stats.put("avgGoalsPerMatch", completedCount > 0 ? Math.round((double) totalGoalsVal / completedCount * 100.0) / 100.0 : 0);

        Long leagueMatches = matchMapper.selectCount(
                new LambdaQueryWrapper<Match>().eq(Match::getMatchType, "league"));
        Long cupMatches = matchMapper.selectCount(
                new LambdaQueryWrapper<Match>().eq(Match::getMatchType, "cup"));
        stats.put("leagueMatches", leagueMatches);
        stats.put("cupMatches", cupMatches);

        Integer totalTeams = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT team_id) FROM (" +
                "  SELECT home_team_id AS team_id FROM matches WHERE home_team_id IS NOT NULL" +
                "  UNION" +
                "  SELECT away_team_id AS team_id FROM matches WHERE away_team_id IS NOT NULL" +
                ") t",
                Integer.class);
        stats.put("totalTeams", totalTeams != null ? totalTeams : 0);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getTopScorers(int limit) {
        String sql = "SELECT team_id, SUM(goals) AS total_goals FROM (" +
                "  SELECT home_team_id AS team_id, home_score AS goals FROM matches WHERE status = 2 AND home_score IS NOT NULL" +
                "  UNION ALL" +
                "  SELECT away_team_id AS team_id, away_score AS goals FROM matches WHERE status = 2 AND away_score IS NOT NULL" +
                ") t GROUP BY team_id ORDER BY total_goals DESC LIMIT ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, limit);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Long teamId = ((Number) row.get("team_id")).longValue();
            int goals = ((Number) row.get("total_goals")).intValue();
            Team team = teamService.getById(teamId);
            Map<String, Object> item = new HashMap<>();
            item.put("teamId", teamId);
            item.put("teamName", team != null ? team.getName() : "Unknown");
            item.put("goals", goals);
            result.add(item);
        }
        return result;
    }
}
