package com.football.community.service;

import java.util.List;
import java.util.Map;

/**
 * 比赛统计服务接口。
 * <p>提供联赛统计、队伍统计、总览统计和射手榜等业务逻辑。</p>
 */
public interface MatchStatsService {

    /**
     * 获取联赛统计数据。
     * @param matchId 赛事ID
     * @return 联赛统计数据
     */
    Map<String, Object> getLeagueStats(Long matchId);

    /**
     * 获取队伍统计数据。
     * @param teamId 队伍ID
     * @return 队伍统计数据列表
     */
    List<Map<String, Object>> getTeamStats(Long teamId);

    /**
     * 获取总体统计数据。
     * @return 总体统计数据
     */
    Map<String, Object> getOverallStats();

    /**
     * 获取射手榜。
     * @param limit 返回数量限制
     * @return 射手列表
     */
    List<Map<String, Object>> getTopScorers(int limit);
}
