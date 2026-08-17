package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Player;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Tag(name = "球员管理", description = "球员CRUD接口")
@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Operation(summary = "获取当前用户的球员信息", description = "获取当前登录用户的球员注册信息")
    @GetMapping("/my")
    public Result<Player> getMyPlayer(@AuthenticationPrincipal CustomUserDetails userDetails) {
        LambdaQueryWrapper<Player> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Player::getUserId, userDetails.getId());
        return Result.success(playerService.getOne(wrapper));
    }

    @Operation(summary = "用户注册为球员", description = "当前用户注册为球员（自助注册）")
    @PostMapping("/register")
    public Result<Player> registerPlayer(@RequestBody Player player,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (playerService.isPlayerRegistered(userDetails.getId())) {
            return Result.error(409, "您已注册为球员");
        }
        player.setUserId(userDetails.getId());
        return Result.success(playerService.createPlayer(player));
    }

    @Operation(summary = "获取球员列表", description = "分页获取球员列表，支持按队伍和关键词筛选")
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
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Player>> getPlayers(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (teamId != null) {
            return Result.success(playerService.getPlayersByTeamId(teamId, page, size));
        }
        return Result.success(playerService.searchPlayers(keyword, page, size));
    }

    @Operation(summary = "获取球员详情", description = "根据ID获取球员详细信息")
    @Parameter(name = "id", description = "球员ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "球员不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{id}")
    public Result<Player> getPlayer(@PathVariable Long id) {
        return Result.success(playerService.getById(id));
    }

    @Operation(summary = "创建球员", description = "新增一名球员")
    @Parameter(description = "球员信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('team:edit')")
    public Result<Player> createPlayer(@RequestBody Player player) {
        return Result.success(playerService.createPlayer(player));
    }

    @Operation(summary = "更新球员信息", description = "修改指定球员的信息")
    @Parameters({
            @Parameter(name = "id", description = "球员ID", required = true),
            @Parameter(description = "球员信息")
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
    public Result<Player> updatePlayer(@PathVariable Long id, @RequestBody Player player) {
        return Result.success(playerService.updatePlayer(id, player));
    }

    @Operation(summary = "删除球员", description = "根据ID删除指定球员")
    @Parameter(name = "id", description = "球员ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('team:delete')")
    public Result<?> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return Result.success();
    }
}
