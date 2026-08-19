package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Coach;

import com.football.community.entity.Team;
import com.football.community.exception.BusinessException;
import com.football.community.repository.CoachMapper;
import com.football.community.service.CoachService;
import com.football.community.service.TeamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CoachServiceImpl extends ServiceImpl<CoachMapper, Coach> implements CoachService {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CoachMapper coachMapper;

    @Override
    public IPage<Coach> getCoachesByTeamId(Long teamId, int page, int size) {
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coach::getTeamId, teamId)
               .orderByAsc(Coach::getName);
        IPage<Coach> coachPage = page(new Page<>(page, size), wrapper);
        coachPage.getRecords().forEach(this::fillNames);
        return coachPage;
    }

    @Override
    public IPage<Coach> searchCoaches(String keyword, int page, int size) {
        LambdaQueryWrapper<Coach> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Coach::getName, keyword)
                   .or().like(Coach::getNationality, keyword);
        }
        wrapper.orderByDesc(Coach::getCreatedAt);
        IPage<Coach> coachPage = page(new Page<>(page, size), wrapper);
        coachPage.getRecords().forEach(this::fillNames);
        return coachPage;
    }

    @Override
    public Coach createCoach(Coach coach) {
        coach.setCreatedAt(LocalDateTime.now());
        save(coach);
        fillNames(coach);
        return coach;
    }

    @Override
    public Coach updateCoach(Long id, Coach coach) {
        Coach existing = getById(id);
        if (existing == null) {
            throw new BusinessException("教练不存在");
        }
        coach.setId(id);
        updateById(coach);
        return getById(id);
    }

    @Override
    public void deleteCoach(Long id) {
        removeById(id);
    }

    @Override
    public boolean isCoachRegistered(Long userId) {
        return coachMapper.existsByUserId(userId) > 0;
    }

    private void fillNames(Coach coach) {
        // 填充球队名称
        if (coach.getTeamId() != null) {
            Team team = teamService.getById(coach.getTeamId());
            if (team != null) {
                coach.setTeamName(team.getName());
            }
        }
    }
}
