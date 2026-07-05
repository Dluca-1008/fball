package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchRegistration;

import java.util.List;

/**
 * 比赛报名服务接口。
 * <p>提供队伍报名、邀请、响应等业务逻辑。</p>
 */
public interface MatchRegistrationService extends IService<MatchRegistration> {

    /**
     * 队伍报名参加比赛。
     * @param matchId 赛事ID
     * @param teamId 队伍ID
     */
    void registerTeam(Long matchId, Long teamId);

    /**
     * 邀请队伍参加比赛。
     * @param matchId 赛事ID
     * @param teamIds 被邀请队伍ID列表
     */
    void inviteTeams(Long matchId, List<Long> teamIds);

    /**
     * 获取指定赛事的所有报名记录。
     * @param matchId 赛事ID
     * @return 报名记录列表
     */
    List<MatchRegistration> getRegistrationsByMatchId(Long matchId);

    /**
     * 响应报名邀请（接受/拒绝）。
     * @param registrationId 报名记录ID
     * @param accept true-接受, false-拒绝
     */
    void respondToRegistration(Long registrationId, boolean accept);
}
