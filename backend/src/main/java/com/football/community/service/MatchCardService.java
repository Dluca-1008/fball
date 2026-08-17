package com.football.community.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.MatchCard;

import java.util.List;

public interface MatchCardService extends IService<MatchCard> {

    List<MatchCard> getCardsByMatchId(Long matchId);

    void addCard(Long matchId, Long teamId, Long playerId, Integer cardType, Integer minute);
}
