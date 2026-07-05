package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Match;
import com.football.community.entity.MatchRegistration;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.MatchService;
import com.football.community.service.MatchRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@Tag(name = "比赛管理", description = "比赛及报名管理接口")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRegistrationService matchRegistrationService;

    @GetMapping
    @Operation(summary = "获取比赛列表", description = "分页查询比赛，支持按球队筛选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<IPage<Match>> getMatches(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "球队ID") @RequestParam(required = false) Long teamId) {
        return Result.success(matchService.getMatchList(page, size, teamId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取比赛详情", description = "根据ID获取比赛详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Match> getMatch(@Parameter(description = "比赛ID", example = "1") @PathVariable Long id) {
        return Result.success(matchService.getMatchDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('match:add')")
    @Operation(summary = "创建比赛", description = "创建新比赛")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Match> createMatch(@Parameter(description = "比赛信息") @RequestBody Match match,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        match.setCreatedBy(userDetails.getId());
        return Result.success(matchService.createMatch(match));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "更新比赛", description = "根据ID更新比赛信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Match> updateMatch(@Parameter(description = "比赛ID", example = "1") @PathVariable Long id, @Parameter(description = "比赛信息") @RequestBody Match match) {
        return Result.success(matchService.updateMatch(id, match));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('match:delete')")
    @Operation(summary = "删除比赛", description = "根据ID删除比赛")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> deleteMatch(@Parameter(description = "比赛ID", example = "1") @PathVariable Long id) {
        matchService.deleteMatch(id);
        return Result.success();
    }

    @PostMapping("/{matchId}/register")
    @Operation(summary = "报名参加比赛", description = "球队报名参加指定比赛")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> registerTeam(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId,
                                  @Parameter(description = "报名信息") @RequestBody Map<String, Object> body,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teamId = ((Number) body.get("teamId")).longValue();
        matchRegistrationService.registerTeam(matchId, teamId);
        return Result.success();
    }

    @PostMapping("/{matchId}/invite")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "邀请球队参赛", description = "邀请指定球队参加比赛")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> inviteTeams(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId,
                                 @Parameter(description = "邀请信息") @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Number> teamIds = (List<Number>) body.get("teamIds");
        List<Long> ids = teamIds.stream().map(Number::longValue).toList();
        matchRegistrationService.inviteTeams(matchId, ids);
        return Result.success();
    }

    @GetMapping("/{matchId}/registrations")
    @Operation(summary = "获取比赛报名列表", description = "获取指定比赛的所有报名信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<MatchRegistration>> getRegistrations(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId) {
        return Result.success(matchRegistrationService.getRegistrationsByMatchId(matchId));
    }

    @PutMapping("/registrations/{registrationId}/respond")
    @Operation(summary = "响应报名", description = "接受或拒绝参赛报名")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> respondToRegistration(@Parameter(description = "报名ID", example = "1") @PathVariable Long registrationId,
                                           @Parameter(description = "响应信息") @RequestBody Map<String, Object> body) {
        Object acceptObj = body.get("accept");
        boolean accept = acceptObj instanceof Boolean ? (Boolean) acceptObj : Boolean.parseBoolean(String.valueOf(acceptObj));
        matchRegistrationService.respondToRegistration(registrationId, accept);
        return Result.success();
    }

    @GetMapping("/schedule")
    @Operation(summary = "获取赛程", description = "按日期范围获取比赛赛程")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<Match>> getSchedule(
            @Parameter(description = "开始时间，格式 yyyy-MM-dd HH:mm:ss") @RequestParam String start,
            @Parameter(description = "结束时间，格式 yyyy-MM-dd HH:mm:ss") @RequestParam String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        return Result.success(matchService.getScheduleByDateRange(startDate, endDate));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将开始的比赛", description = "获取最近即将进行的比赛列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<List<Match>> getUpcoming(@Parameter(description = "返回数量", example = "10") @RequestParam(defaultValue = "10") int limit) {
        return Result.success(matchService.getUpcomingMatches(limit));
    }

    @PostMapping("/{matchId}/generate-schedule")
    @PreAuthorize("hasAuthority('match:edit')")
    @Operation(summary = "生成赛程", description = "自动生成小组赛或联赛赛程")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> generateSchedule(@Parameter(description = "比赛ID", example = "1") @PathVariable Long matchId,
                                      @Parameter(description = "赛程生成信息") @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Number> teamIdNums = (List<Number>) body.get("teamIds");
        List<Long> teamIds = teamIdNums.stream().map(Number::longValue).toList();
        String startDateStr = (String) body.get("startDate");
        int intervalDays = body.get("intervalDays") != null ? ((Number) body.get("intervalDays")).intValue() : 7;

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(startDateStr, formatter);

        String matchType = (String) body.get("matchType");
        List<Match> generated;
        if ("cup".equals(matchType)) {
            @SuppressWarnings("unchecked")
            Map<String, List<Number>> groupMapNums = (Map<String, List<Number>>) body.get("groupTeams");
            Map<String, List<Long>> groupTeams = new HashMap<>();
            groupMapNums.forEach((k, v) -> groupTeams.put(k, v.stream().map(Number::longValue).toList()));
            generated = matchService.generateCupGroupSchedule(matchId, groupTeams, startDate, intervalDays);
        } else {
            generated = matchService.generateLeagueSchedule(matchId, teamIds, startDate, intervalDays);
        }

        return Result.success(generated.size());
    }
}
