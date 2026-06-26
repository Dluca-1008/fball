package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.MatchKnockout;
import com.football.community.entity.Team;
import com.football.community.exception.BusinessException;
import com.football.community.repository.MatchKnockoutMapper;
import com.football.community.service.MatchKnockoutService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchKnockoutServiceImpl extends ServiceImpl<MatchKnockoutMapper, MatchKnockout> implements MatchKnockoutService {

    @Autowired
    private TeamService teamService;

    @Override
    @Transactional
    public void initKnockoutBracket(Long matchId, List<Long> teamIds) {
        LambdaQueryWrapper<MatchKnockout> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(MatchKnockout::getMatchId, matchId);
        remove(deleteWrapper);

        int totalRounds = (int) (Math.log(teamIds.size()) / Math.log(2));
        String[] roundNames = {"决赛", "半决赛", "1/4决赛", "1/8决赛", "1/16决赛"};
        int roundIndex = 0;

        List<List<Long>> currentRoundTeams = new ArrayList<>();
        List<Long> firstRoundTeams = new ArrayList<>(teamIds);
        while (firstRoundTeams.size() % 2 != 0) {
            firstRoundTeams.add(null);
        }
        currentRoundTeams.add(firstRoundTeams);

        List<Long> nextRoundTeams = new ArrayList<>();
        for (int i = 0; i < firstRoundTeams.size(); i += 2) {
            Long team1 = firstRoundTeams.get(i);
            Long team2 = firstRoundTeams.get(i + 1);
            if (team1 != null && team2 != null) {
                nextRoundTeams.add(null);
            } else if (team1 != null) {
                nextRoundTeams.add(team1);
            } else {
                nextRoundTeams.add(team2);
            }
        }

        for (int round = 0; round < totalRounds; round++) {
            int matchesInRound = (int) Math.pow(2, totalRounds - round - 1);
            String roundName = round < roundNames.length ? roundNames[round] : "第" + (round + 1) + "轮";

            for (int pos = 0; pos < matchesInRound; pos++) {
                MatchKnockout knockout = new MatchKnockout();
                knockout.setMatchId(matchId);
                knockout.setRound(roundName);
                knockout.setPosition(pos);
                knockout.setStatus(0);
                knockout.setHomeScore(0);
                knockout.setAwayScore(0);
                knockout.setCreatedAt(LocalDateTime.now());

                if (round == totalRounds - 1) {
                    int teamIdx = pos * 2;
                    if (teamIdx < teamIds.size()) {
                        knockout.setHomeTeamId(teamIds.get(teamIdx));
                    }
                    if (teamIdx + 1 < teamIds.size()) {
                        knockout.setAwayTeamId(teamIds.get(teamIdx + 1));
                    }
                }
                save(knockout);
            }
        }

        linkKnockoutMatches(matchId);
    }

    private void linkKnockoutMatches(Long matchId) {
        LambdaQueryWrapper<MatchKnockout> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchKnockout::getMatchId, matchId)
               .orderByAsc(MatchKnockout::getRound)
               .orderByAsc(MatchKnockout::getPosition);
        List<MatchKnockout> allMatches = list(wrapper);

        List<String> rounds = allMatches.stream()
                .map(MatchKnockout::getRound)
                .distinct()
                .toList();

        for (int i = 0; i < rounds.size() - 1; i++) {
            String currentRound = rounds.get(i);
            String nextRound = rounds.get(i + 1);

            List<MatchKnockout> currentRoundMatches = allMatches.stream()
                    .filter(m -> m.getRound().equals(currentRound))
                    .toList();
            List<MatchKnockout> nextRoundMatches = allMatches.stream()
                    .filter(m -> m.getRound().equals(nextRound))
                    .toList();

            for (int j = 0; j < currentRoundMatches.size(); j++) {
                int nextIdx = j / 2;
                if (nextIdx < nextRoundMatches.size()) {
                    MatchKnockout current = currentRoundMatches.get(j);
                    current.setNextMatchId(nextRoundMatches.get(nextIdx).getId());
                    updateById(current);
                }
            }
        }
    }

    @Override
    public List<MatchKnockout> getKnockoutBracket(Long matchId) {
        LambdaQueryWrapper<MatchKnockout> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchKnockout::getMatchId, matchId)
               .orderByAsc(MatchKnockout::getRound)
               .orderByAsc(MatchKnockout::getPosition);
        List<MatchKnockout> knockouts = list(wrapper);
        knockouts.forEach(this::fillTeamNames);
        return knockouts;
    }

    @Override
    @Transactional
    public void updateKnockoutResult(Long knockoutId, int homeScore, int awayScore) {
        MatchKnockout knockout = getById(knockoutId);
        if (knockout == null) {
            throw new BusinessException("淘汰赛记录不存在");
        }
        knockout.setHomeScore(homeScore);
        knockout.setAwayScore(awayScore);
        knockout.setStatus(2);
        updateById(knockout);
    }

    @Override
    @Transactional
    public void advanceWinner(Long knockoutId) {
        MatchKnockout knockout = getById(knockoutId);
        if (knockout == null) {
            throw new BusinessException("淘汰赛记录不存在");
        }
        if (knockout.getHomeScore() == null || knockout.getAwayScore() == null) {
            throw new BusinessException("请先录入比分");
        }

        Long winnerId;
        if (knockout.getHomeScore() > knockout.getAwayScore()) {
            winnerId = knockout.getHomeTeamId();
        } else if (knockout.getAwayScore() > knockout.getHomeScore()) {
            winnerId = knockout.getAwayTeamId();
        } else {
            throw new BusinessException("平局需要进行加时赛或点球大战，请手动指定胜者");
        }

        knockout.setWinnerTeamId(winnerId);
        updateById(knockout);

        if (knockout.getNextMatchId() != null) {
            MatchKnockout nextMatch = getById(knockout.getNextMatchId());
            if (nextMatch != null) {
                if (nextMatch.getHomeTeamId() == null) {
                    nextMatch.setHomeTeamId(winnerId);
                } else {
                    nextMatch.setAwayTeamId(winnerId);
                }
                updateById(nextMatch);
            }
        }
    }

    private void fillTeamNames(MatchKnockout knockout) {
        if (knockout.getHomeTeamId() != null) {
            Team team = teamService.getById(knockout.getHomeTeamId());
            if (team != null) knockout.setHomeTeamName(team.getName());
        }
        if (knockout.getAwayTeamId() != null) {
            Team team = teamService.getById(knockout.getAwayTeamId());
            if (team != null) knockout.setAwayTeamName(team.getName());
        }
        if (knockout.getWinnerTeamId() != null) {
            Team team = teamService.getById(knockout.getWinnerTeamId());
            if (team != null) knockout.setWinnerTeamName(team.getName());
        }
    }
}
