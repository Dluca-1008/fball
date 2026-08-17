package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.entity.MatchFriendRequest;

import java.util.List;

public interface MatchFriendRequestService {

    /**
     * 发起友谊赛邀请
     */
    MatchFriendRequest createRequest(Long senderTeamId, Long receiverTeamId, Long createdBy, String venue, java.time.LocalDateTime matchDate);

    /**
     * 获取当前球队的发起邀请列表
     */
    List<MatchFriendRequest> getSentRequests(Long teamId);

    /**
     * 获取当前球队的收到邀请列表
     */
    List<MatchFriendRequest> getReceivedRequests(Long teamId);

    /**
     * 响应邀请（接受或拒绝）
     */
    MatchFriendRequest respond(Long requestId, Long responderTeamId, Boolean accept);

    /**
     * 获取赛事下的友谊赛列表
     */
    List<MatchFriendRequest> getFriendRequestsByMatch(Long matchId);

    /**
     * 获取当前用户的球队ID
     */
    Long getCurrentUserTeamId(Long userId);
}
