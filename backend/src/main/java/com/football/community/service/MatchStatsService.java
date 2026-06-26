package com.football.community.service;

import java.util.List;
import java.util.Map;

public interface MatchStatsService {

    Map<String, Object> getLeagueStats(Long matchId);

    List<Map<String, Object>> getTeamStats(Long teamId);

    Map<String, Object> getOverallStats();

    List<Map<String, Object>> getTopScorers(int limit);
}
