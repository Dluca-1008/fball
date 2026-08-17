package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.football.community.entity.Match;
import com.football.community.entity.MatchFriendRequest;
import com.football.community.exception.BusinessException;
import com.football.community.repository.MatchFriendRequestMapper;
import com.football.community.repository.MatchMapper;
import com.football.community.service.MatchFriendRequestService;
import com.football.community.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchFriendRequestServiceImpl implements MatchFriendRequestService {

    @Autowired
    private MatchFriendRequestMapper requestMapper;

    @Autowired
    private MatchMapper matchMapper;

    @Autowired
    private TeamService teamService;

    @Override
    @Transactional
    public MatchFriendRequest createRequest(Long senderTeamId, Long receiverTeamId, Long createdBy, String venue, LocalDateTime matchDate) {
        // 检查是否已有未处理的邀请（同一方向）
        LambdaQueryWrapper<MatchFriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchFriendRequest::getSenderTeamId, senderTeamId)
               .eq(MatchFriendRequest::getReceiverTeamId, receiverTeamId)
               .eq(MatchFriendRequest::getStatus, 0);
        if (requestMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("已存在未处理的邀请");
        }
        // 检查反向邀请是否也存在
        wrapper.and(w -> w.eq(MatchFriendRequest::getSenderTeamId, receiverTeamId)
                          .eq(MatchFriendRequest::getReceiverTeamId, senderTeamId)
                          .eq(MatchFriendRequest::getStatus, 0));
        if (requestMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("双方已互相发送了邀请，请等待对方处理");
        }

        MatchFriendRequest request = new MatchFriendRequest();
        request.setSenderTeamId(senderTeamId);
        request.setReceiverTeamId(receiverTeamId);
        request.setMatchDate(matchDate);
        request.setVenue(venue);
        request.setStatus(0);
        request.setCreatedBy(createdBy);
        requestMapper.insert(request);
        return request;
    }

    @Override
    public List<MatchFriendRequest> getSentRequests(Long teamId) {
        LambdaQueryWrapper<MatchFriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchFriendRequest::getSenderTeamId, teamId)
               .orderByDesc(MatchFriendRequest::getCreatedAt);
        return requestMapper.selectList(wrapper);
    }

    @Override
    public List<MatchFriendRequest> getReceivedRequests(Long teamId) {
        LambdaQueryWrapper<MatchFriendRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MatchFriendRequest::getReceiverTeamId, teamId)
               .orderByDesc(MatchFriendRequest::getCreatedAt);
        return requestMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public MatchFriendRequest respond(Long requestId, Long responderTeamId, Boolean accept) {
        MatchFriendRequest request = requestMapper.selectById(requestId);
        if (request == null) {
            throw new BusinessException("邀请不存在");
        }
        if (!request.getReceiverTeamId().equals(responderTeamId)) {
            throw new BusinessException("无权操作此邀请");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException("该邀请已处理");
        }

        request.setStatus(accept ? 1 : 2);
        requestMapper.updateById(request);

        // 如果接受，自动创建友谊赛
        if (accept) {
            // 检查是否已存在相同对阵的未开始比赛（双向检查）
            LambdaQueryWrapper<Match> matchWrapper = new LambdaQueryWrapper<>();
            matchWrapper.and(w -> w
                    .eq(Match::getHomeTeamId, request.getSenderTeamId())
                    .eq(Match::getAwayTeamId, request.getReceiverTeamId())
                    .or().eq(Match::getHomeTeamId, request.getReceiverTeamId())
                    .eq(Match::getAwayTeamId, request.getSenderTeamId())
            ).in(Match::getStatus, 0, 1);
            if (matchMapper.selectCount(matchWrapper) > 0) {
                throw new BusinessException("该对阵已存在进行中的友谊赛");
            }

            Match match = new Match();
            match.setName("友谊赛");
            match.setMatchType("friendly");
            match.setHomeTeamId(request.getSenderTeamId());
            match.setAwayTeamId(request.getReceiverTeamId());
            match.setMatchDate(request.getMatchDate());
            match.setVenue(request.getVenue());
            match.setStatus(0);
            match.setHomeScore(0);
            match.setAwayScore(0);
            match.setHomeScoreHalf(0);
            match.setAwayScoreHalf(0);
            match.setCreatedBy(request.getCreatedBy());
            match.setCreatedAt(LocalDateTime.now());
            matchMapper.insert(match);
        }

        return request;
    }

    @Override
    public List<MatchFriendRequest> getFriendRequestsByMatch(Long matchId) {
        return List.of();
    }

    @Override
    public Long getCurrentUserTeamId(Long userId) {
        return teamService.getUserTeamId(userId);
    }
}
