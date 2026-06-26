package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MatchService extends IService<Match> {

    IPage<Match> getMatchList(int page, int size, Long teamId);

    Match getMatchDetail(Long id);

    Match createMatch(Match match);

    Match updateMatch(Long id, Match match);

    void deleteMatch(Long id);

    List<Match> getScheduleByDateRange(LocalDateTime start, LocalDateTime end);

    List<Match> getUpcomingMatches(int limit);

    List<Match> generateLeagueSchedule(Long matchId, List<Long> teamIds, LocalDateTime startDate, int intervalDays);

    List<Match> generateCupGroupSchedule(Long matchId, Map<String, List<Long>> groupTeams, LocalDateTime startDate, int intervalDays);
}
