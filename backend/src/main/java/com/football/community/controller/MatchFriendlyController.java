package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.MatchFriendRequest;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.MatchFriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "友谊赛邀请", description = "友谊赛邀请接口")
@RestController
@RequestMapping("/api/matches/friendly")
public class MatchFriendlyController {

    @Autowired
    private MatchFriendRequestService friendRequestService;

    @GetMapping("/sent")
    @Operation(summary = "获取已发出的邀请")
    @ApiResponse(responseCode = "200", description = "成功")
    public Result<List<MatchFriendRequest>> getSentRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teamId = friendRequestService.getCurrentUserTeamId(userDetails.getId());
        if (teamId == null) return Result.success(List.of());
        return Result.success(friendRequestService.getSentRequests(teamId));
    }

    @GetMapping("/received")
    @Operation(summary = "获取收到的邀请")
    @ApiResponse(responseCode = "200", description = "成功")
    public Result<List<MatchFriendRequest>> getReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teamId = friendRequestService.getCurrentUserTeamId(userDetails.getId());
        if (teamId == null) return Result.success(List.of());
        return Result.success(friendRequestService.getReceivedRequests(teamId));
    }

    @PostMapping("/invite")
    @Operation(summary = "发起友谊赛邀请")
    @ApiResponse(responseCode = "200", description = "成功")
    public Result<?> createInvite(
            @Parameter(description = "邀请信息") @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long receiverTeamId = body.get("receiverTeamId") != null ? ((Number) body.get("receiverTeamId")).longValue() : null;
        String venue = body.get("venue") != null ? body.get("venue").toString() : null;
        java.time.LocalDateTime matchDate = null;
        if (body.get("matchDate") != null) {
            matchDate = java.time.LocalDateTime.parse(body.get("matchDate").toString());
        }
        Long senderTeamId = friendRequestService.getCurrentUserTeamId(userDetails.getId());
        if (senderTeamId == null) {
            return Result.error("您还未加入球队");
        }
        if (receiverTeamId == null) {
            return Result.error("请选择邀请球队");
        }
        friendRequestService.createRequest(senderTeamId, receiverTeamId, userDetails.getId(), venue, matchDate);
        return Result.success();
    }

    @PutMapping("/requests/{requestId}/respond")
    @Operation(summary = "响应友谊赛邀请")
    @ApiResponse(responseCode = "200", description = "成功")
    public Result<?> respondInvite(
            @Parameter(description = "邀请ID") @PathVariable Long requestId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "接受/拒绝") @RequestBody Map<String, Object> body) {
        Long teamId = friendRequestService.getCurrentUserTeamId(userDetails.getId());
        if (teamId == null) {
            return Result.error("您还未加入球队");
        }
        boolean accept = body.get("accept") != null && Boolean.parseBoolean(body.get("accept").toString());
        friendRequestService.respond(requestId, teamId, accept);
        return Result.success();
    }
}
