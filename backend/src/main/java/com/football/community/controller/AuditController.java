package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.AuditService;
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

@Tag(name = "审核管理", description = "内容审核接口")
@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @Operation(summary = "审核帖子", description = "对指定帖子进行审核操作（通过/拒绝）")
    @Parameters({
            @Parameter(name = "postId", description = "帖子ID", required = true),
            @Parameter(name = "action", description = "审核动作(1-通过,2-拒绝)", required = true),
            @Parameter(name = "reason", description = "拒绝原因")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "审核成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/post/{postId}")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<?> auditPost(@PathVariable Long postId,
                               @RequestParam Integer action,
                               @RequestParam(required = false) String reason,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        auditService.auditPost(postId, action, reason, userDetails.getId());
        return Result.success();
    }

    @Operation(summary = "审核评论", description = "对指定评论进行审核操作（通过/拒绝）")
    @Parameters({
            @Parameter(name = "commentId", description = "评论ID", required = true),
            @Parameter(name = "action", description = "审核动作(1-通过,2-拒绝)", required = true),
            @Parameter(name = "reason", description = "拒绝原因")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "审核成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/comment/{commentId}")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<?> auditComment(@PathVariable Long commentId,
                                  @RequestParam Integer action,
                                  @RequestParam(required = false) String reason,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        auditService.auditComment(commentId, action, reason, userDetails.getId());
        return Result.success();
    }

    @Operation(summary = "获取审核日志", description = "分页获取内容审核记录")
    @Parameters({
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10"),
            @Parameter(name = "targetType", description = "目标类型过滤")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<com.football.community.entity.AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer targetType) {
        return Result.success(auditService.getAuditLogs(page, size, targetType));
    }
}
