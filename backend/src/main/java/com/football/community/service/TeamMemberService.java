package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.TeamApplication;
import com.football.community.entity.TeamInvitation;
import com.football.community.entity.TeamMember;

import java.util.List;
import java.util.Map;

public interface TeamMemberService extends IService<TeamMember> {

    List<Map<String, Object>> getMembersByTeamId(Long teamId);

    List<TeamApplication> getApplicationsByTeamId(Long teamId);

    List<Map<String, Object>> getMyInvitations(Long userId);

    void respondToInvitation(Long invitationId, boolean accept, Long userId, String memberType, Map<String, Object> memberInfo);

    void applyToJoin(Long teamId, Long userId, String reason, String memberType, Map<String, Object> memberInfo);

    String inviteMember(Long teamId, Long userId, Long inviterId);

    void approveApplication(Long teamId, Long userId, Integer status, Long reviewerId, String memberType, Map<String, Object> memberInfo);

    void removeMember(Long teamId, Long userId, Long operatorId);

    void setMemberRole(Long teamId, Long userId, String role, Long operatorId);

    void setMemberStatus(Long teamId, Long userId, Integer status, Long operatorId);
}
