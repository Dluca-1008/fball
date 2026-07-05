package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Match;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 比赛服务接口。
 * <p>提供比赛CRUD、赛程生成（联赛/杯赛）、报名管理等业务逻辑。</p>
 */
public interface MatchService extends IService<Match> {

    /**
     * 分页获取比赛列表。
     * @param page 页码
     * @param size 每页大小
     * @param teamId 队伍ID筛选
     * @return 比赛分页结果
     */
    IPage<Match> getMatchList(int page, int size, Long teamId);

    /**
     * 获取比赛详情。
     * @param id 比赛ID
     * @return 比赛实体
     */
    Match getMatchDetail(Long id);

    /**
     * 创建新比赛。
     * @param match 比赛实体
     * @return 创建后的比赛
     */
    Match createMatch(Match match);

    /**
     * 更新比赛信息。
     * @param id 比赛ID
     * @param match 比赛实体
     * @return 更新后的比赛
     */
    Match updateMatch(Long id, Match match);

    /**
     * 删除比赛。
     * @param id 比赛ID
     */
    void deleteMatch(Long id);

    /**
     * 按日期范围获取赛程。
     * @param start 开始时间
     * @param end 结束时间
     * @return 赛程列表
     */
    List<Match> getScheduleByDateRange(LocalDateTime start, LocalDateTime end);

    /**
     * 获取即将进行的比赛。
     * @param limit 返回数量限制
     * @return 比赛列表
     */
    List<Match> getUpcomingMatches(int limit);

    /**
     * 生成联赛赛程。
     * @param matchId 赛事ID
     * @param teamIds 参赛队伍ID列表
     * @param startDate 开始日期
     * @param intervalDays 间隔天数
     * @return 生成的赛程列表
     */
    List<Match> generateLeagueSchedule(Long matchId, List<Long> teamIds, LocalDateTime startDate, int intervalDays);

    /**
     * 生成杯赛小组赛赛程。
     * @param matchId 赛事ID
     * @param groupTeams 分组队伍映射
     * @param startDate 开始日期
     * @param intervalDays 间隔天数
     * @return 生成的赛程列表
     */
    List<Match> generateCupGroupSchedule(Long matchId, Map<String, List<Long>> groupTeams, LocalDateTime startDate, int intervalDays);
}
