package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Coach;
import com.football.community.service.CoachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "教练管理", description = "教练CRUD接口")
@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    @Autowired
    private CoachService coachService;

    @Operation(summary = "获取教练列表", description = "分页获取教练列表，支持按队伍和关键词筛选")
    @Parameters({
            @Parameter(name = "teamId", description = "队伍ID筛选"),
            @Parameter(name = "keyword", description = "搜索关键词"),
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Coach>> getCoaches(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (teamId != null) {
            return Result.success(coachService.getCoachesByTeamId(teamId, page, size));
        }
        return Result.success(coachService.searchCoaches(keyword, page, size));
    }

    @Operation(summary = "获取教练详情", description = "根据ID获取教练详细信息")
    @Parameter(name = "id", description = "教练ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "教练不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public Result<Coach> getCoach(@PathVariable Long id) {
        return Result.success(coachService.getById(id));
    }

    @Operation(summary = "创建教练", description = "新增一名教练")
    @Parameter(description = "教练信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Coach> createCoach(@RequestBody Coach coach) {
        return Result.success(coachService.createCoach(coach));
    }

    @Operation(summary = "更新教练信息", description = "修改指定教练的信息")
    @Parameters({
            @Parameter(name = "id", description = "教练ID", required = true),
            @Parameter(description = "教练信息")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Coach> updateCoach(@PathVariable Long id, @RequestBody Coach coach) {
        return Result.success(coachService.updateCoach(id, coach));
    }

    @Operation(summary = "删除教练", description = "根据ID删除指定教练")
    @Parameter(name = "id", description = "教练ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('team:delete')")
    public Result<?> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return Result.success();
    }
}
