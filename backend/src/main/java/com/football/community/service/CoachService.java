package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Coach;

public interface CoachService extends IService<Coach> {

    IPage<Coach> getCoachesByTeamId(Long teamId, int page, int size);

    IPage<Coach> searchCoaches(String keyword, int page, int size);

    Coach createCoach(Coach coach);

    Coach updateCoach(Long id, Coach coach);

    void deleteCoach(Long id);
}
