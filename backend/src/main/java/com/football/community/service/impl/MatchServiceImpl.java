package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Match;
import com.football.community.entity.Team;
import com.football.community.exception.BusinessException;
import com.football.community.repository.MatchMapper;
import com.football.community.service.MatchService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MatchServiceImpl extends ServiceImpl<MatchMapper, Match> implements MatchService {

    @Autowired
    private TeamService teamService;

    @Override
    public IPage<Match> getMatchList(int page, int size, Long teamId) {
        LambdaQueryWrapper<Match> wrapper = new LambdaQueryWrapper<>();

        if (teamId != null) {
            wrapper.and(w -> w.eq(Match::getHomeTeamId, teamId)
                             .or().eq(Match::getAwayTeamId, teamId));
        }

        wrapper.orderByDesc(Match::getMatchDate);
        IPage<Match> matchPage = page(new Page<>(page, size), wrapper);

        fillMatchInfoBatch(matchPage.getRecords());

        return matchPage;
    }

    @Override
    public Match getMatchDetail(Long id) {
        Match match = getById(id);
        if (match == null) {
            throw new BusinessException("赛事不存在");
        }
        fillMatchInfo(match);
        return match;
    }

    @Override
    public Match createMatch(Match match) {
        match.setStatus(0);
        match.setHomeScore(0);
        match.setAwayScore(0);
        match.setHomeScoreHalf(0);
        match.setAwayScoreHalf(0);
        match.setCreatedAt(LocalDateTime.now());
        save(match);
        return match;
    }

    @Override
    public Match updateMatch(Long id, Match match) {
        Match existing = getById(id);
        if (existing == null) {
            throw new BusinessException("赛事不存在");
        }
        match.setId(id);
        updateById(match);
        return getById(id);
    }

    @Override
    public void deleteMatch(Long id) {
        removeById(id);
    }

    @Override
    public List<Match> getScheduleByDateRange(LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Match> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Match::getMatchDate, start)
               .le(Match::getMatchDate, end)
               .orderByAsc(Match::getMatchDate);
        List<Match> matches = list(wrapper);
        fillMatchInfoBatch(matches);
        return matches;
    }

    @Override
    public List<Match> getUpcomingMatches(int limit) {
        LambdaQueryWrapper<Match> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(Match::getMatchDate, LocalDateTime.now())
               .orderByAsc(Match::getMatchDate)
               .last("LIMIT " + limit);
        List<Match> matches = list(wrapper);
        fillMatchInfoBatch(matches);
        return matches;
    }

    @Override
    @Transactional
    public List<Match> generateLeagueSchedule(Long matchId, List<Long> teamIds, LocalDateTime startDate, int intervalDays) {
        LambdaQueryWrapper<Match> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Match::getLeagueId, matchId);
        remove(deleteWrapper);

        List<Match> generated = new ArrayList<>();
        int n = teamIds.size();

        List<List<Long[]>> rounds = generateRoundRobinRounds(teamIds);

        LocalDateTime currentDate = startDate;
        for (List<Long[]> round : rounds) {
            for (Long[] pair : round) {
                Match match = new Match();
                match.setLeagueId(matchId);
                match.setMatchType("league");
                match.setHomeTeamId(pair[0]);
                match.setAwayTeamId(pair[1]);
                match.setMatchDate(currentDate);
                match.setStatus(0);
                match.setHomeScore(0);
                match.setAwayScore(0);
                match.setHomeScoreHalf(0);
                match.setAwayScoreHalf(0);
                match.setCreatedAt(LocalDateTime.now());
                save(match);
                generated.add(match);
            }
            currentDate = currentDate.plusDays(intervalDays);
        }

        fillMatchInfoBatch(generated);
        return generated;
    }

    @Override
    @Transactional
    public List<Match> generateCupGroupSchedule(Long matchId, Map<String, List<Long>> groupTeams, LocalDateTime startDate, int intervalDays) {
        LambdaQueryWrapper<Match> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(Match::getLeagueId, matchId);
        remove(deleteWrapper);

        List<Match> generated = new ArrayList<>();
        LocalDateTime currentDate = startDate;

        for (Map.Entry<String, List<Long>> entry : groupTeams.entrySet()) {
            String groupName = entry.getKey();
            List<Long> teams = entry.getValue();

            List<Long[]> roundRobinPairs = generatePairs(teams);

            for (Long[] pair : roundRobinPairs) {
                Match match = new Match();
                match.setLeagueId(matchId);
                match.setMatchType("cup");
                match.setHomeTeamId(pair[0]);
                match.setAwayTeamId(pair[1]);
                match.setMatchDate(currentDate);
                match.setStatus(0);
                match.setHomeScore(0);
                match.setAwayScore(0);
                match.setHomeScoreHalf(0);
                match.setAwayScoreHalf(0);
                match.setCreatedAt(LocalDateTime.now());
                save(match);
                generated.add(match);

                currentDate = currentDate.plusDays(intervalDays);
            }
        }

        fillMatchInfoBatch(generated);
        return generated;
    }

    private List<List<Long[]>> generateRoundRobinRounds(List<Long> teamIds) {
        List<List<Long[]>> allRounds = new ArrayList<>();
        List<Long> teams = new ArrayList<>(teamIds);

        if (teams.size() % 2 != 0) {
            teams.add(null);
        }

        int n = teams.size();
        int totalRounds = n - 1;
        int matchesPerRound = n / 2;

        List<Long> rotating = new ArrayList<>(teams.subList(1, n));

        for (int round = 0; round < totalRounds; round++) {
            List<Long[]> roundMatches = new ArrayList<>();

            Long fixed = teams.get(0);
            for (int i = 0; i < matchesPerRound; i++) {
                Long home = rotating.get(i);
                Long away = rotating.get(rotating.size() - 1 - i);

                if (home != null && away != null) {
                    roundMatches.add(new Long[]{home, away});
                }
            }

            allRounds.add(roundMatches);

            if (rotating.size() > 1) {
                Long last = rotating.remove(rotating.size() - 1);
                rotating.add(1, last);
            }
        }

        return allRounds;
    }

    private List<Long[]> generatePairs(List<Long> teamIds) {
        List<Long[]> pairs = new ArrayList<>();
        for (int i = 0; i < teamIds.size(); i++) {
            for (int j = i + 1; j < teamIds.size(); j++) {
                pairs.add(new Long[]{teamIds.get(i), teamIds.get(j)});
            }
        }
        return pairs;
    }

    private void fillMatchInfo(Match match) {
        if (match.getHomeTeamId() != null) {
            Team homeTeam = teamService.getById(match.getHomeTeamId());
            if (homeTeam != null) {
                match.setHomeTeamName(homeTeam.getName());
            }
        }
        if (match.getAwayTeamId() != null) {
            Team awayTeam = teamService.getById(match.getAwayTeamId());
            if (awayTeam != null) {
                match.setAwayTeamName(awayTeam.getName());
            }
        }
    }

    private void fillMatchInfoBatch(List<Match> matches) {
        if (matches == null || matches.isEmpty()) return;
        Set<Long> teamIds = new HashSet<>();
        for (Match match : matches) {
            if (match.getHomeTeamId() != null) teamIds.add(match.getHomeTeamId());
            if (match.getAwayTeamId() != null) teamIds.add(match.getAwayTeamId());
        }
        List<Team> teams = teamService.listByIds(teamIds);
        Map<Long, Team> teamMap = new HashMap<>();
        for (Team team : teams) {
            teamMap.put(team.getId(), team);
        }
        for (Match match : matches) {
            if (match.getHomeTeamId() != null) {
                Team homeTeam = teamMap.get(match.getHomeTeamId());
                if (homeTeam != null) match.setHomeTeamName(homeTeam.getName());
            }
            if (match.getAwayTeamId() != null) {
                Team awayTeam = teamMap.get(match.getAwayTeamId());
                if (awayTeam != null) match.setAwayTeamName(awayTeam.getName());
            }
        }
    }
}
