package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.MatchGroup;
import com.football.community.entity.MatchKnockout;
import com.football.community.service.MatchGroupService;
import com.football.community.service.MatchKnockoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
public class MatchGroupController {

    @Autowired
    private MatchGroupService matchGroupService;

    @Autowired
    private MatchKnockoutService matchKnockoutService;

    @PostMapping("/{matchId}/groups/init")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<?> initGroups(@PathVariable Long matchId,
                                @RequestBody Map<String, List<Long>> groupTeamMap) {
        matchGroupService.initGroups(matchId, groupTeamMap);
        return Result.success();
    }

    @GetMapping("/{matchId}/groups")
    public Result<List<MatchGroup>> getGroups(@PathVariable Long matchId) {
        return Result.success(matchGroupService.getGroupsByMatchId(matchId));
    }

    @GetMapping("/{matchId}/groups/standings")
    public Result<Map<String, List<MatchGroup>>> getGroupStandings(@PathVariable Long matchId) {
        return Result.success(matchGroupService.getGroupStandings(matchId));
    }

    @PostMapping("/{matchId}/knockouts/init")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<?> initKnockouts(@PathVariable Long matchId,
                                   @RequestBody Map<String, List<Long>> body) {
        List<Long> teamIds = body.get("teamIds");
        matchKnockoutService.initKnockoutBracket(matchId, teamIds);
        return Result.success();
    }

    @GetMapping("/{matchId}/knockouts")
    public Result<List<MatchKnockout>> getKnockouts(@PathVariable Long matchId) {
        return Result.success(matchKnockoutService.getKnockoutBracket(matchId));
    }

    @PutMapping("/knockouts/{knockoutId}/score")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<?> updateKnockoutScore(@PathVariable Long knockoutId,
                                         @RequestBody Map<String, Object> body) {
        int homeScore = ((Number) body.get("homeScore")).intValue();
        int awayScore = ((Number) body.get("awayScore")).intValue();
        matchKnockoutService.updateKnockoutResult(knockoutId, homeScore, awayScore);
        return Result.success();
    }

    @PutMapping("/knockouts/{knockoutId}/advance")
    @PreAuthorize("hasAuthority('match:edit')")
    public Result<?> advanceWinner(@PathVariable Long knockoutId) {
        matchKnockoutService.advanceWinner(knockoutId);
        return Result.success();
    }
}
