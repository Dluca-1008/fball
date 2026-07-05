package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.service.MatchStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "比赛统计", description = "比赛数据统计接口")
@RestController
@RequestMapping("/api/match-stats")
public class MatchStatsController {

    @Autowired
    private MatchStatsService matchStatsService;

    @Operation(summary = "获取联赛统计", description = "获取指定赛事的联赛统计数据")
    @Parameter(name = "matchId", description = "赛事ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "赛事不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/league/{matchId}")
    public Result<Map<String, Object>> getLeagueStats(@PathVariable Long matchId) {
        return Result.success(matchStatsService.getLeagueStats(matchId));
    }

    @Operation(summary = "获取队伍统计", description = "获取指定队伍的统计数据")
    @Parameter(name = "teamId", description = "队伍ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "队伍不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/team/{teamId}")
    public Result<List<Map<String, Object>>> getTeamStats(@PathVariable Long teamId) {
        return Result.success(matchStatsService.getTeamStats(teamId));
    }

    @Operation(summary = "获取总览统计", description = "获取所有赛事的总体统计数据")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/overall")
    public Result<Map<String, Object>> getOverallStats() {
        return Result.success(matchStatsService.getOverallStats());
    }

    @Operation(summary = "获取射手榜", description = "获取指定赛事的射手排行榜")
    @Parameters({
            @Parameter(name = "limit", description = "返回数量限制", example = "10")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/top-scorers")
    public Result<List<Map<String, Object>>> getTopScorers(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(matchStatsService.getTopScorers(limit));
    }
}
