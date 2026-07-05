package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.MatchGroup;
import com.football.community.entity.MatchKnockout;
import com.football.community.service.MatchGroupService;
import com.football.community.service.MatchKnockoutService;
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

@RestController
@RequestMapping("/api/matches")
@Tag(name = "比赛分组", description = "比赛分组和淘汰赛接口")
public class MatchGroupController {

    @Autowired
    private MatchGroupService matchGroupService;

    @Autowired
    private MatchKnockoutService matchKnockoutService;

    @PostMapping("/{matchId}/groups/init")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "初始化分组", description = "为比赛初始化小组循环赛分组")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> initGroups(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId,
                                @Parameter(description = "分组队伍映射") @RequestBody Map<String, List<Long>> groupTeamMap) {
        matchGroupService.initGroups(matchId, groupTeamMap);
        return Result.success();
    }

    @GetMapping("/{matchId}/groups")
    @Operation(summary = "获取分组列表", description = "获取指定比赛的小组列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<MatchGroup>> getGroups(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId) {
        return Result.success(matchGroupService.getGroupsByMatchId(matchId));
    }

    @GetMapping("/{matchId}/groups/standings")
    @Operation(summary = "获取小组积分榜", description = "获取指定比赛各小组的积分排名")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Map<String, List<MatchGroup>>> getGroupStandings(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId) {
        return Result.success(matchGroupService.getGroupStandings(matchId));
    }

    @PostMapping("/{matchId}/knockouts/init")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "初始化淘汰赛", description = "为比赛初始化淘汰赛对阵表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> initKnockouts(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId,
                                   @Parameter(description = "晋级队伍ID列表") @RequestBody Map<String, List<Long>> body) {
        List<Long> teamIds = body.get("teamIds");
        matchKnockoutService.initKnockoutBracket(matchId, teamIds);
        return Result.success();
    }

    @GetMapping("/{matchId}/knockouts")
    @Operation(summary = "获取淘汰赛对阵", description = "获取指定比赛的淘汰赛对阵表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<MatchKnockout>> getKnockouts(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId) {
        return Result.success(matchKnockoutService.getKnockoutBracket(matchId));
    }

    @PutMapping("/knockouts/{knockoutId}/score")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "更新淘汰赛比分", description = "更新淘汰赛场次比分并自动判定晋级")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> updateKnockoutScore(@Parameter(description = "淘汰赛场次ID", example = "1") @PathVariable Long knockoutId,
                                         @Parameter(description = "比分信息") @RequestBody Map<String, Object> body) {
        int homeScore = ((Number) body.get("homeScore")).intValue();
        int awayScore = ((Number) body.get("awayScore")).intValue();
        matchKnockoutService.updateKnockoutResult(knockoutId, homeScore, awayScore);
        return Result.success();
    }

    @PutMapping("/knockouts/{knockoutId}/advance")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "手动晋级", description = "手动指定胜者进入下一轮")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> advanceWinner(@Parameter(description = "淘汰赛场次ID", example = "1") @PathVariable Long knockoutId) {
        matchKnockoutService.advanceWinner(knockoutId);
        return Result.success();
    }
}
