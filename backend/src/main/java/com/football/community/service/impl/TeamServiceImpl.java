package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Coach;
import com.football.community.entity.Match;
import com.football.community.entity.Player;
import com.football.community.entity.Team;
import com.football.community.entity.TeamMember;
import com.football.community.exception.BusinessException;
import com.football.community.repository.CoachMapper;
import com.football.community.repository.MatchMapper;
import com.football.community.repository.PlayerMapper;
import com.football.community.repository.RoleMapper;
import com.football.community.repository.TeamMapper;
import com.football.community.repository.TeamMemberMapper;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Autowired
    private TeamMemberMapper teamMemberMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private MatchMapper matchMapper;

    @Override
    public IPage<Team> getTeamList(int page, int size, String keyword) {
        LambdaQueryWrapper<Team> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Team::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Team::getName, keyword)
                   .or().like(Team::getCity, keyword);
        }
        wrapper.orderByDesc(Team::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public Team createTeam(Team team, Long userId, String memberType, Map<String, Object> memberInfo) {
        team.setCreatedBy(userId);
        team.setStatus(1);
        team.setCreatedAt(LocalDateTime.now());
        save(team);

        TeamMember member = new TeamMember();
        member.setTeamId(team.getId());
        member.setUserId(userId);
        member.setRole("admin");
        member.setMemberType(memberType != null ? memberType : "player");
        member.setStatus(1);
        member.setJoinTime(LocalDateTime.now());
        member.setCreatedAt(LocalDateTime.now());
        teamMemberMapper.insert(member);

        if ("player".equals(memberType) && memberInfo != null) {
            Player player = new Player();
            player.setUserId(userId);
            player.setTeamId(team.getId());
            player.setName((String) memberInfo.get("name"));
            player.setPosition((String) memberInfo.get("position"));
            player.setNumber(memberInfo.get("number") != null ? ((Number) memberInfo.get("number")).intValue() : null);
            player.setNationality((String) memberInfo.get("nationality"));
            player.setCreatedAt(LocalDateTime.now());
            playerMapper.insert(player);
        } else if ("coach".equals(memberType) && memberInfo != null) {
            Coach coach = new Coach();
            coach.setUserId(userId);
            coach.setTeamId(team.getId());
            coach.setName((String) memberInfo.get("name"));
            coach.setRoleTitle((String) memberInfo.get("roleTitle"));
            coach.setNationality((String) memberInfo.get("nationality"));
            coach.setExperienceYears(memberInfo.get("experienceYears") != null ? ((Number) memberInfo.get("experienceYears")).intValue() : null);
            coach.setCreatedAt(LocalDateTime.now());
            coachMapper.insert(coach);
        }

        var teamAdminRole = roleMapper.selectByCode("team_admin");
        if (teamAdminRole != null) {
            roleMapper.assignRoleIfNotExists(userId, teamAdminRole.getId());
        }

        return team;
    }

    @Override
    public Team updateTeam(Long id, Team team) {
        Team existing = getById(id);
        if (existing == null) {
            throw new BusinessException("球队不存在");
        }
        team.setId(id);
        updateById(team);
        return getById(id);
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        LambdaQueryWrapper<TeamMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(TeamMember::getTeamId, id);
        teamMemberMapper.delete(memberWrapper);

        LambdaQueryWrapper<Player> playerWrapper = new LambdaQueryWrapper<>();
        playerWrapper.eq(Player::getTeamId, id);
        playerMapper.delete(playerWrapper);

        LambdaQueryWrapper<Coach> coachWrapper = new LambdaQueryWrapper<>();
        coachWrapper.eq(Coach::getTeamId, id);
        coachMapper.delete(coachWrapper);

        removeById(id);
    }

    @Override
    public boolean isTeamAdmin(Long teamId, Long userId) {
        return teamMemberMapper.isTeamAdmin(teamId, userId);
    }

    @Override
    public boolean isTeamMember(Long teamId, Long userId) {
        return teamMemberMapper.isTeamMember(teamId, userId);
    }

    @Override
    @Transactional
    public void dissolveTeam(Long teamId, Long userId) {
        if (!isTeamAdmin(teamId, userId)) {
            throw new BusinessException("只有球队管理员才能解散球队");
        }

        LambdaQueryWrapper<Match> matchWrapper = new LambdaQueryWrapper<>();
        matchWrapper.and(w -> w.eq(Match::getHomeTeamId, teamId).or().eq(Match::getAwayTeamId, teamId))
                    .in(Match::getStatus, 0, 1);
        long unfinishedCount = matchMapper.selectCount(matchWrapper);
        if (unfinishedCount > 0) {
            throw new BusinessException("该球队还有未结束的比赛，无法解散");
        }

        Team team = getById(teamId);
        team.setStatus(0);
        updateById(team);
    }

    @Override
    public Long getUserTeamId(Long userId) {
        return teamMemberMapper.getTeamIdByUserId(userId);
    }

    @Override
    public List<Long> getUserTeamIds(Long userId) {
        return teamMemberMapper.getTeamIdsByUserId(userId);
    }
}
