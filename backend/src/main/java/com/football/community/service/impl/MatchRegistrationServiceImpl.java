package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.MatchRegistration;
import com.football.community.entity.Team;
import com.football.community.exception.BusinessException;
import com.football.community.repository.MatchRegistrationMapper;
import com.football.community.service.MatchRegistrationService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchRegistrationServiceImpl extends ServiceImpl<MatchRegistrationMapper, MatchRegistration> implements MatchRegistrationService {

    @Autowired
    private TeamService teamService;

    @Override
    @Transactional
    public void registerTeam(Long matchId, Long teamId) {
        Team team = teamService.getById(teamId);
        if (team == null) {
            throw new BusinessException("球队不存在");
        }

        LambdaQueryWrapper<MatchRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchRegistration::getMatchId, matchId)
               .eq(MatchRegistration::getTeamId, teamId);
        long count = count(wrapper);
        if (count > 0) {
            throw new BusinessException("该球队已报名或已收到邀请");
        }

        MatchRegistration registration = new MatchRegistration();
        registration.setMatchId(matchId);
        registration.setTeamId(teamId);
        registration.setStatus(0);
        registration.setCreatedAt(LocalDateTime.now());
        save(registration);
    }

    @Override
    @Transactional
    public void inviteTeams(Long matchId, List<Long> teamIds) {
        for (Long teamId : teamIds) {
            LambdaQueryWrapper<MatchRegistration> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MatchRegistration::getMatchId, matchId)
                   .eq(MatchRegistration::getTeamId, teamId);
            long count = count(wrapper);
            if (count > 0) {
                continue;
            }

            MatchRegistration registration = new MatchRegistration();
            registration.setMatchId(matchId);
            registration.setTeamId(teamId);
            registration.setStatus(0);
            registration.setCreatedAt(LocalDateTime.now());
            save(registration);
        }
    }

    @Override
    public List<MatchRegistration> getRegistrationsByMatchId(Long matchId) {
        LambdaQueryWrapper<MatchRegistration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchRegistration::getMatchId, matchId)
               .orderByDesc(MatchRegistration::getCreatedAt);
        List<MatchRegistration> registrations = list(wrapper);

        registrations.forEach(r -> {
            Team team = teamService.getById(r.getTeamId());
            if (team != null) {
                r.setTeamName(team.getName());
            }
        });

        return registrations;
    }

    @Override
    @Transactional
    public void respondToRegistration(Long registrationId, boolean accept) {
        MatchRegistration registration = getById(registrationId);
        if (registration == null) {
            throw new BusinessException("报名记录不存在");
        }
        if (registration.getStatus() != 0) {
            throw new BusinessException("该报名已处理");
        }

        registration.setStatus(accept ? 1 : 2);
        updateById(registration);
    }
}
