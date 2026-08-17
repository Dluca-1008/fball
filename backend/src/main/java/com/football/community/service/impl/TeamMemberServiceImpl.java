package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Coach;
import com.football.community.entity.Player;
import com.football.community.entity.Team;
import com.football.community.entity.TeamApplication;
import com.football.community.entity.TeamInvitation;
import com.football.community.entity.TeamMember;
import com.football.community.exception.BusinessException;
import com.football.community.repository.CoachMapper;
import com.football.community.repository.PlayerMapper;
import com.football.community.repository.TeamApplicationMapper;
import com.football.community.repository.TeamInvitationMapper;
import com.football.community.repository.TeamMemberMapper;
import com.football.community.service.TeamMemberService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamMemberServiceImpl extends ServiceImpl<TeamMemberMapper, TeamMember> implements TeamMemberService {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private TeamInvitationMapper teamInvitationMapper;

    @Autowired
    private TeamApplicationMapper teamApplicationMapper;

    @Autowired
    private com.football.community.service.PlayerService playerService;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private com.football.community.repository.UserMapper userMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Override
    public List<Map<String, Object>> getMembersByTeamId(Long teamId) {
        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getStatus, 1);
        List<TeamMember> members = list(wrapper);

        return members.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", m.getUserId());
            map.put("username", m.getUserId()); // fallback
            // 查询用户名
            com.football.community.entity.User user = userMapper.selectById(m.getUserId());
            if (user != null) {
                map.put("username", user.getUsername());
                map.put("nickname", user.getNickname());
            }
            map.put("role", m.getRole());
            map.put("memberType", m.getMemberType());
            map.put("status", m.getStatus());
            map.put("joinTime", m.getJoinTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<TeamApplication> getApplicationsByTeamId(Long teamId) {
        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, teamId)
               .orderByDesc(TeamApplication::getCreatedAt);
        return teamApplicationMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> getMyInvitations(Long userId) {
        LambdaQueryWrapper<TeamInvitation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamInvitation::getInviteeId, userId)
               .orderByDesc(TeamInvitation::getCreatedAt);
        List<TeamInvitation> invitations = teamInvitationMapper.selectList(wrapper);

        return invitations.stream().map(inv -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", inv.getId());
            map.put("teamId", inv.getTeamId());
            map.put("inviterId", inv.getInviterId());
            map.put("inviteCode", inv.getInviteCode());
            map.put("status", inv.getStatus());
            map.put("expireTime", inv.getExpireTime());
            map.put("createdAt", inv.getCreatedAt());

            Team team = teamService.getById(inv.getTeamId());
            if (team != null) {
                map.put("teamName", team.getName());
            }
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void respondToInvitation(Long invitationId, boolean accept, Long userId, String memberType, Map<String, Object> memberInfo) {
        TeamInvitation invitation = teamInvitationMapper.selectById(invitationId);
        if (invitation == null) {
            throw new BusinessException("邀请不存在");
        }
        if (!invitation.getInviteeId().equals(userId)) {
            throw new BusinessException("无权操作此邀请");
        }
        if (invitation.getStatus() != 0) {
            throw new BusinessException("邀请已处理");
        }
        if (invitation.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("邀请已过期");
        }

        if (accept) {
            invitation.setStatus(1);
            TeamMember member = new TeamMember();
            member.setTeamId(invitation.getTeamId());
            member.setUserId(userId);
            member.setRole("member");
            member.setMemberType(memberType != null ? memberType : "player");
            member.setStatus(1);
            member.setInviteBy(invitation.getInviterId());
            member.setJoinTime(LocalDateTime.now());
            member.setCreatedAt(LocalDateTime.now());
            save(member);

            createMemberProfile(invitation.getTeamId(), userId, memberType, memberInfo);
        } else {
            invitation.setStatus(2);
        }
        invitation.setExpireTime(invitation.getExpireTime());
        teamInvitationMapper.updateById(invitation);
    }

    @Override
    @Transactional
    public void applyToJoin(Long teamId, Long userId, String reason, String memberType, Map<String, Object> memberInfo) {
        boolean isMember = teamService.isTeamMember(teamId, userId);
        // 检查用户是否已注册为球员
        if (!playerService.isPlayerRegistered(userId)) {
            throw new BusinessException("您尚未注册为球员，请先在球员管理页面注册");
        }
        if (isMember) {
            throw new BusinessException("您已是该球队成员");
        }

        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, teamId)
               .eq(TeamApplication::getUserId, userId)
               .eq(TeamApplication::getStatus, 0);
        long count = teamApplicationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("您已提交申请，请等待审核");
        }

        TeamApplication application = new TeamApplication();
        application.setTeamId(teamId);
        application.setUserId(userId);
        application.setReason(reason);
        application.setMemberType(memberType != null ? memberType : "player");
        application.setStatus(0);
        application.setCreatedAt(LocalDateTime.now());
        teamApplicationMapper.insert(application);
    }

    @Override
    @Transactional
    public String inviteMember(Long teamId, Long userId, Long inviterId) {
        boolean isMember = teamService.isTeamMember(teamId, userId);
        // 检查用户是否已注册为球员
        if (!playerService.isPlayerRegistered(userId)) {
            throw new BusinessException("您尚未注册为球员，请先在球员管理页面注册");
        }
        if (isMember) {
            throw new BusinessException("该用户已是球队成员");
        }

        boolean isAdmin = teamService.isTeamAdmin(teamId, inviterId);
        if (!isAdmin) {
            throw new BusinessException("只有球队管理员才能邀请成员");
        }

        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TeamInvitation invitation = new TeamInvitation();
        invitation.setTeamId(teamId);
        invitation.setInviterId(inviterId);
        invitation.setInviteeId(userId);
        invitation.setInviteCode(inviteCode);
        invitation.setStatus(0);
        invitation.setExpireTime(LocalDateTime.now().plusDays(7));
        invitation.setCreatedAt(LocalDateTime.now());
        teamInvitationMapper.insert(invitation);

        return inviteCode;
    }

    @Override
    @Transactional
    public void approveApplication(Long teamId, Long userId, Integer status, Long reviewerId, String memberType, Map<String, Object> memberInfo) {
        boolean isAdmin = teamService.isTeamAdmin(teamId, reviewerId);
        if (!isAdmin) {
            throw new BusinessException("只有球队管理员才能审批申请");
        }

        LambdaQueryWrapper<TeamApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamApplication::getTeamId, teamId)
               .eq(TeamApplication::getUserId, userId)
               .eq(TeamApplication::getStatus, 0);
        TeamApplication application = teamApplicationMapper.selectOne(wrapper);

        if (application == null) {
            throw new BusinessException("申请记录不存在");
        }

        application.setStatus(status);
        application.setReviewerId(reviewerId);
        application.setReviewTime(LocalDateTime.now());
        teamApplicationMapper.updateById(application);

        if (status == 1) {
            String effectiveMemberType = memberType != null ? memberType : application.getMemberType();
            if (effectiveMemberType == null) effectiveMemberType = "player";

            TeamMember member = new TeamMember();
            member.setTeamId(teamId);
            member.setUserId(userId);
            member.setRole("member");
            member.setMemberType(effectiveMemberType);
            member.setStatus(1);
            member.setInviteBy(reviewerId);
            member.setJoinTime(LocalDateTime.now());
            member.setCreatedAt(LocalDateTime.now());
            save(member);

            createMemberProfile(teamId, userId, effectiveMemberType, memberInfo);
        }
    }

    @Override
    @Transactional
    public void removeMember(Long teamId, Long userId, Long operatorId) {
        boolean isAdmin = teamService.isTeamAdmin(teamId, operatorId);
        if (!isAdmin && !userId.equals(operatorId)) {
            throw new BusinessException("没有权限执行此操作");
        }

        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getUserId, userId);
        TeamMember member = getOne(wrapper);

        if (member == null) {
            throw new BusinessException("成员不存在");
        }

        removeById(member.getId());
    }

    @Override
    @Transactional
    public void setMemberRole(Long teamId, Long userId, String role, Long operatorId) {
        boolean isAdmin = teamService.isTeamAdmin(teamId, operatorId);
        if (!isAdmin) {
            throw new BusinessException("只有球队管理员才能设置成员角色");
        }

        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getUserId, userId)
               .eq(TeamMember::getStatus, 1);
        TeamMember member = getOne(wrapper);

        if (member == null) {
            throw new BusinessException("成员不存在");
        }

        member.setRole(role);
        member.setUpdatedAt(LocalDateTime.now());
        updateById(member);
    }

    @Override
    @Transactional
    public void setMemberStatus(Long teamId, Long userId, Integer status, Long operatorId) {
        boolean isAdmin = teamService.isTeamAdmin(teamId, operatorId);
        if (!isAdmin) {
            throw new BusinessException("只有球队管理员才能修改成员状态");
        }

        LambdaQueryWrapper<TeamMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TeamMember::getTeamId, teamId)
               .eq(TeamMember::getUserId, userId);
        TeamMember member = getOne(wrapper);

        if (member == null) {
            throw new BusinessException("成员不存在");
        }

        member.setStatus(status);
        member.setUpdatedAt(LocalDateTime.now());
        updateById(member);
    }

    private void createMemberProfile(Long teamId, Long userId, String memberType, Map<String, Object> memberInfo) {
        if (memberInfo == null) return;

        if ("player".equals(memberType)) {
            Player player = new Player();
            player.setTeamId(teamId);
            player.setName((String) memberInfo.get("name"));
            player.setPosition((String) memberInfo.get("position"));
            player.setNumber(memberInfo.get("number") != null ? ((Number) memberInfo.get("number")).intValue() : null);
            player.setNationality((String) memberInfo.get("nationality"));
            player.setCreatedAt(LocalDateTime.now());
            playerMapper.insert(player);
        } else if ("coach".equals(memberType)) {
            Coach coach = new Coach();
            coach.setTeamId(teamId);
            coach.setName((String) memberInfo.get("name"));
            coach.setRoleTitle((String) memberInfo.get("roleTitle"));
            coach.setNationality((String) memberInfo.get("nationality"));
            coach.setExperienceYears(memberInfo.get("experienceYears") != null ? ((Number) memberInfo.get("experienceYears")).intValue() : null);
            coach.setCreatedAt(LocalDateTime.now());
            coachMapper.insert(coach);
        }
    }
}
