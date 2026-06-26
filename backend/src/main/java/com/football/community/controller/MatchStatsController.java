package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.service.MatchStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/match-stats")
public class MatchStatsController {

    @Autowired
    private MatchStatsService matchStatsService;

    @GetMapping("/league/{matchId}")
    public Result<Map<String, Object>> getLeagueStats(@PathVariable Long matchId) {
        return Result.success(matchStatsService.getLeagueStats(matchId));
    }

    @GetMapping("/team/{teamId}")
    public Result<List<Map<String, Object>>> getTeamStats(@PathVariable Long teamId) {
        return Result.success(matchStatsService.getTeamStats(teamId));
    }

    @GetMapping("/overall")
    public Result<Map<String, Object>> getOverallStats() {
        return Result.success(matchStatsService.getOverallStats());
    }

    @GetMapping("/top-scorers")
    public Result<List<Map<String, Object>>> getTopScorers(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(matchStatsService.getTopScorers(limit));
    }
}
