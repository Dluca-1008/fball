package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Match;
import com.football.community.entity.MatchRegistration;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.MatchService;
import com.football.community.service.MatchRegistrationService;
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
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MatchRegistrationService matchRegistrationService;

    @GetMapping
    public Result<IPage<Match>> getMatches(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long teamId) {
        return Result.success(matchService.getMatchList(page, size, teamId));
    }

    @GetMapping("/{id}")
    public Result<Match> getMatch(@PathVariable Long id) {
        return Result.success(matchService.getMatchDetail(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('match:add')")
    public Result<Match> createMatch(@RequestBody Match match,
                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        match.setCreatedBy(userDetails.getId());
        return Result.success(matchService.createMatch(match));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<Match> updateMatch(@PathVariable Long id, @RequestBody Match match) {
        return Result.success(matchService.updateMatch(id, match));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('match:delete')")
    public Result<?> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return Result.success();
    }

    @PostMapping("/{matchId}/register")
    public Result<?> registerTeam(@PathVariable Long matchId,
                                  @RequestBody Map<String, Object> body,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long teamId = ((Number) body.get("teamId")).longValue();
        matchRegistrationService.registerTeam(matchId, teamId);
        return Result.success();
    }

    @PostMapping("/{matchId}/invite")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<?> inviteTeams(@PathVariable Long matchId,
                                 @RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Number> teamIds = (List<Number>) body.get("teamIds");
        List<Long> ids = teamIds.stream().map(Number::longValue).toList();
        matchRegistrationService.inviteTeams(matchId, ids);
        return Result.success();
    }

    @GetMapping("/{matchId}/registrations")
    public Result<List<MatchRegistration>> getRegistrations(@PathVariable Long matchId) {
        return Result.success(matchRegistrationService.getRegistrationsByMatchId(matchId));
    }

    @PutMapping("/registrations/{registrationId}/respond")
    public Result<?> respondToRegistration(@PathVariable Long registrationId,
                                           @RequestBody Map<String, Object> body) {
        Object acceptObj = body.get("accept");
        boolean accept = acceptObj instanceof Boolean ? (Boolean) acceptObj : Boolean.parseBoolean(String.valueOf(acceptObj));
        matchRegistrationService.respondToRegistration(registrationId, accept);
        return Result.success();
    }

    @GetMapping("/schedule")
    public Result<List<Match>> getSchedule(
            @RequestParam String start,
            @RequestParam String end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(start, formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, formatter);
        return Result.success(matchService.getScheduleByDateRange(startDate, endDate));
    }

    @GetMapping("/upcoming")
    public Result<List<Match>> getUpcoming(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(matchService.getUpcomingMatches(limit));
    }

    @PostMapping("/{matchId}/generate-schedule")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<?> generateSchedule(@PathVariable Long matchId,
                                      @RequestBody Map<String, Object> body) {
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
