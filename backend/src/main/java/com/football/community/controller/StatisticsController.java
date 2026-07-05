package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "数据统计", description = "统计数据接口")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Operation(summary = "管理员概览", description = "获取系统管理员数据概览")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/admin/overview")
    @PreAuthorize("hasAuthority('stat:manage')")
    public Result<Map<String, Object>> getAdminOverview() {
        return Result.success(statisticsService.getAdminOverview());
    }

    @Operation(summary = "用户统计", description = "获取注册用户统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('stat:manage')")
    public Result<Map<String, Object>> getUserStatistics() {
        return Result.success(statisticsService.getUserStatistics());
    }

    @Operation(summary = "比赛统计", description = "获取比赛相关统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/match")
    @PreAuthorize("hasAuthority('stat:match')")
    public Result<Map<String, Object>> getMatchStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(statisticsService.getMatchStatistics());
    }

    @Operation(summary = "销售统计", description = "获取商品销售统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('stat:sales')")
    public Result<Map<String, Object>> getSalesStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(statisticsService.getSalesStatistics(userDetails.getId()));
    }

    @Operation(summary = "队伍统计", description = "获取指定队伍的统计数据")
    @Parameter(name = "teamId", description = "队伍ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "队伍不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/team/{teamId}")
    public Result<Map<String, Object>> getTeamStatistics(@PathVariable Long teamId) {
        return Result.success(statisticsService.getTeamStatistics(teamId));
    }
}
