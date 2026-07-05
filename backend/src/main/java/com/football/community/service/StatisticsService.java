package com.football.community.service;

import java.util.Map;

/**
 * 统计服务接口。
 * <p>提供管理员概览、用户统计、比赛统计、销售统计和队伍统计等业务逻辑。</p>
 */
public interface StatisticsService {

    /**
     * 获取管理员数据概览。
     * @return 概览统计数据
     */
    Map<String, Object> getAdminOverview();

    /**
     * 获取用户统计数据。
     * @return 用户统计数据
     */
    Map<String, Object> getUserStatistics();

    /**
     * 获取比赛统计数据。
     * @return 比赛统计数据
     */
    Map<String, Object> getMatchStatistics();

    /**
     * 获取销售统计数据。
     * @param merchantId 商家ID
     * @return 销售统计数据
     */
    Map<String, Object> getSalesStatistics(Long merchantId);

    /**
     * 获取队伍统计数据。
     * @param teamId 队伍ID
     * @return 队伍统计数据
     */
    Map<String, Object> getTeamStatistics(Long teamId);
}
