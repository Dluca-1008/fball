package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/admin/overview")
    @PreAuthorize("hasAuthority('stat:manage')")
    public Result<Map<String, Object>> getAdminOverview() {
        return Result.success(statisticsService.getAdminOverview());
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('stat:manage')")
    public Result<Map<String, Object>> getUserStatistics() {
        return Result.success(statisticsService.getUserStatistics());
    }

    @GetMapping("/match")
    @PreAuthorize("hasAuthority('stat:match')")
    public Result<Map<String, Object>> getMatchStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(statisticsService.getMatchStatistics());
    }

    @GetMapping("/sales")
    @PreAuthorize("hasAuthority('stat:sales')")
    public Result<Map<String, Object>> getSalesStatistics(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(statisticsService.getSalesStatistics(userDetails.getId()));
    }

    @GetMapping("/team/{teamId}")
    public Result<Map<String, Object>> getTeamStatistics(@PathVariable Long teamId) {
        return Result.success(statisticsService.getTeamStatistics(teamId));
    }
}
