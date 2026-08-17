package com.football.community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.MatchCard;
import com.football.community.repository.MatchCardMapper;
import com.football.community.service.MatchCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchCardServiceImpl extends ServiceImpl<MatchCardMapper, MatchCard> implements MatchCardService {

    @Autowired
    private MatchCardMapper matchCardMapper;

    @Override
    public List<MatchCard> getCardsByMatchId(Long matchId) {
        return matchCardMapper.selectWithPlayerInfo(matchId);
    }

    @Override
    public void addCard(Long matchId, Long teamId, Long playerId, Integer cardType, Integer minute) {
        MatchCard card = new MatchCard();
        card.setMatchId(matchId);
        card.setTeamId(teamId);
        card.setPlayerId(playerId);
        card.setCardType(cardType);
        card.setMinute(minute);
        card.setCreatedAt(LocalDateTime.now());
        matchCardMapper.insert(card);
    }
}
