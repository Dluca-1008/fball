package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchRegistration;

import java.util.List;

public interface MatchRegistrationService extends IService<MatchRegistration> {

    void registerTeam(Long matchId, Long teamId);

    void inviteTeams(Long matchId, List<Long> teamIds);

    List<MatchRegistration> getRegistrationsByMatchId(Long matchId);

    void respondToRegistration(Long registrationId, boolean accept);
}
