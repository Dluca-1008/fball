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

        // 确保队伍数为2的幂次，不足则补null
        List<Long> teams = new ArrayList<>(teamIds);
        while (teams.size() % 2 != 0) {
            teams.add(null);
        }
        int totalTeams = teams.size();
        // 总轮数 = log2(队伍数)，最少1轮
        int totalRounds = Math.max(1, (int) (Math.log(totalTeams) / Math.log(2)));

        String[] roundNames = {"决赛", "半决赛", "1/4决赛", "1/8决赛", "1/16决赛"};

        // 从第一轮(1/8决赛等)到决赛，逐轮创建比赛
        for (int round = 0; round < totalRounds; round++) {
            // 轮次索引：round=0 是第一轮（参赛队数最多），round=totalRounds-1 是决赛
            int matchesInRound = totalTeams / (int) Math.pow(2, round + 1);
            // 轮次名称从后往前取：决赛在最前面
            int nameIdx = totalRounds - 1 - round;
            String roundName = nameIdx < roundNames.length ? roundNames[nameIdx] : "第" + (round + 1) + "轮";

            for (int pos = 0; pos < matchesInRound; pos++) {
                MatchKnockout knockout = new MatchKnockout();
                knockout.setMatchId(matchId);
                knockout.setRound(roundName);
                knockout.setPosition(pos);
                knockout.setStatus(0);
                knockout.setHomeScore(0);
                knockout.setAwayScore(0);
                knockout.setCreatedAt(LocalDateTime.now());

                // 只有第一轮填充真实队伍，后续轮次由晋级逻辑自动填充
                if (round == 0) {
                    int teamIdx = pos * 2;
                    if (teamIdx < totalTeams && teams.get(teamIdx) != null) {
                        knockout.setHomeTeamId(teams.get(teamIdx));
                    }
                    if (teamIdx + 1 < totalTeams && teams.get(teamIdx + 1) != null) {
                        knockout.setAwayTeamId(teams.get(teamIdx + 1));
                    }
                }
                // 后续轮次 homeTeamId/awayTeamId 为 null，等待晋级时填充
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
        if (winnerId == null) {
            throw new BusinessException("无法确定胜者，请手动指定");
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
