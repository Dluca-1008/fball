package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.TeamApplication;
import com.football.community.entity.TeamInvitation;
import com.football.community.entity.TeamMember;

import java.util.List;
import java.util.Map;

/**
 * 队伍成员服务接口。
 * <p>提供成员管理、申请审批、邀请响应、角色设置等业务逻辑。</p>
 */
public interface TeamMemberService extends IService<TeamMember> {

    /**
     * 获取队伍成员列表。
     * @param teamId 队伍ID
     * @return 成员列表
     */
    List<Map<String, Object>> getMembersByTeamId(Long teamId);

    /**
     * 获取队伍入队申请列表（含申请人球员/教练信息）。
     * @param teamId 队伍ID
     * @return 申请列表
     */
    List<Map<String, Object>> getApplicationsByTeamId(Long teamId);

    /**
     * 获取当前用户的入队邀请列表。
     * @param userId 用户ID
     * @return 邀请列表
     */
    List<Map<String, Object>> getMyInvitations(Long userId);

    /**
     * 响应入队邀请。
     * @param invitationId 邀请ID
     * @param accept true-接受, false-拒绝
     * @param userId 用户ID
     * @param memberType 成员类型
     * @param memberInfo 成员信息
     */
    void respondToInvitation(Long invitationId, boolean accept, Long userId, String memberType, Map<String, Object> memberInfo);

    /**
     * 申请加入队伍。
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @param reason 申请理由
     * @param memberType 成员类型
     * @param memberInfo 成员信息
     */
    void applyToJoin(Long teamId, Long userId, String reason, String memberType, Map<String, Object> memberInfo);

    /**
     * 邀请用户加入队伍。
     * @param teamId 队伍ID
     * @param userId 被邀请用户ID
     * @param inviterId 邀请人ID
     * @return 邀请ID
     */
    String inviteMember(Long teamId, Long userId, Long inviterId);

    /**
     * 审批入队申请。
     * @param teamId 队伍ID
     * @param userId 申请人ID
     * @param status 审批状态
     * @param reviewerId 审批人ID
     * @param memberType 成员类型
     * @param memberInfo 成员信息
     */
    void approveApplication(Long teamId, Long userId, Integer status, Long reviewerId, String memberType, Map<String, Object> memberInfo);

    /**
     * 移除队伍成员。
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @param operatorId 操作人ID
     */
    void removeMember(Long teamId, Long userId, Long operatorId);

    /**
     * 设置成员角色。
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @param role 角色名称
     * @param operatorId 操作人ID
     */
    void setMemberRole(Long teamId, Long userId, String role, Long operatorId);

    /**
     * 设置成员状态。
     * @param teamId 队伍ID
     * @param userId 用户ID
     * @param status 状态值
     * @param operatorId 操作人ID
     */
    void setMemberStatus(Long teamId, Long userId, Integer status, Long operatorId);
}
