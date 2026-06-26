package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchKnockout;

import java.util.List;

public interface MatchKnockoutService extends IService<MatchKnockout> {

    void initKnockoutBracket(Long matchId, List<Long> teamIds);

    List<MatchKnockout> getKnockoutBracket(Long matchId);

    void updateKnockoutResult(Long knockoutId, int homeScore, int awayScore);

    void advanceWinner(Long knockoutId);
}
