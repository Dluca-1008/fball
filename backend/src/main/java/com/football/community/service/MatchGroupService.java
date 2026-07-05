package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchGroup;

import java.util.List;
import java.util.Map;

/**
 * 比赛分组服务接口。
 * <p>提供小组赛分组初始化、积分榜更新等业务逻辑。</p>
 */
public interface MatchGroupService extends IService<MatchGroup> {

    /**
     * 初始化小组赛的分组。
     * @param matchId 赛事ID
     * @param groupTeamMap 分组映射（组名 -> 队伍ID列表）
     */
    void initGroups(Long matchId, Map<String, List<Long>> groupTeamMap);

    /**
     * 获取指定赛事的小组列表。
     * @param matchId 赛事ID
     * @return 小组列表
     */
    List<MatchGroup> getGroupsByMatchId(Long matchId);

    /**
     * 获取指定赛事的小组积分榜。
     * @param matchId 赛事ID
     * @return 小组积分榜映射
     */
    Map<String, List<MatchGroup>> getGroupStandings(Long matchId);

    /**
     * 更新小组赛比赛结果。
     * @param matchId 赛事ID
     * @param groupName 组名
     * @param homeTeamId 主队ID
     * @param awayTeamId 客队ID
     * @param homeScore 主队得分
     * @param awayScore 客队得分
     */
    void updateGroupResult(Long matchId, String groupName, Long homeTeamId, Long awayTeamId, int homeScore, int awayScore);

    /**
     * 获取小组出线队伍。
     * @param matchId 赛事ID
     * @param groupName 组名
     * @param topN 出线名额
     * @return 出线队伍ID列表
     */
    List<Long> getTopTeamsFromGroup(Long matchId, String groupName, int topN);
}
