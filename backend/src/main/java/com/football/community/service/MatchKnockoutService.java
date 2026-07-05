package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchKnockout;

import java.util.List;

/**
 * 比赛淘汰赛服务接口。
 * <p>提供淘汰赛 bracket 初始化、比分更新、胜者晋级等业务逻辑。</p>
 */
public interface MatchKnockoutService extends IService<MatchKnockout> {

    /**
     * 初始化淘汰赛 bracket。
     * @param matchId 赛事ID
     * @param teamIds 参赛队伍ID列表
     */
    void initKnockoutBracket(Long matchId, List<Long> teamIds);

    /**
     * 获取淘汰赛 bracket。
     * @param matchId 赛事ID
     * @return 淘汰赛对阵列表
     */
    List<MatchKnockout> getKnockoutBracket(Long matchId);

    /**
     * 更新淘汰赛比分。
     * @param knockoutId 淘汰赛轮次ID
     * @param homeScore 主队得分
     * @param awayScore 客队得分
     */
    void updateKnockoutResult(Long knockoutId, int homeScore, int awayScore);

    /**
     * 晋级胜者到下一轮。
     * @param knockoutId 淘汰赛轮次ID
     */
    void advanceWinner(Long knockoutId);
}
