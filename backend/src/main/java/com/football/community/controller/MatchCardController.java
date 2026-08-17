package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.MatchCard;
import com.football.community.service.MatchCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "比赛红黄牌", description = "比赛红黄牌记录接口")
@RestController
@RequestMapping("/api/matches")
public class MatchCardController {

    @Autowired
    private MatchCardService matchCardService;

    @GetMapping("/{matchId}/cards")
    @Operation(summary = "获取比赛红黄牌", description = "获取指定比赛的红黄牌记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<MatchCard>> getCards(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId) {
        return Result.success(matchCardService.getCardsByMatchId(matchId));
    }

    @PostMapping("/{matchId}/cards")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "添加红黄牌", description = "为指定比赛添加红黄牌记录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> addCard(
            @Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId,
            @Parameter(description = "红黄牌信息") @RequestBody Map<String, Object> body) {
        Long teamId = body.get("teamId") != null ? ((Number) body.get("teamId")).longValue() : null;
        if (teamId == null) {
            return Result.error("请选择球队");
        }
        Long playerId = body.get("playerId") != null ? ((Number) body.get("playerId")).longValue() : null;
        Integer cardType = body.get("cardType") != null ? ((Number) body.get("cardType")).intValue() : 1;
        Integer minute = body.get("minute") != null ? ((Number) body.get("minute")).intValue() : null;
        matchCardService.addCard(matchId, teamId, playerId, cardType, minute);
        return Result.success();
    }
}
