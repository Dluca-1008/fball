package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.AuditLog;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @PostMapping("/post/{postId}")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<?> auditPost(@PathVariable Long postId,
                               @RequestParam Integer action,
                               @RequestParam(required = false) String reason,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        auditService.auditPost(postId, action, reason, userDetails.getId());
        return Result.success();
    }

    @PostMapping("/comment/{commentId}")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<?> auditComment(@PathVariable Long commentId,
                                  @RequestParam Integer action,
                                  @RequestParam(required = false) String reason,
                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        auditService.auditComment(commentId, action, reason, userDetails.getId());
        return Result.success();
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<IPage<AuditLog>> getAuditLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer targetType) {
        return Result.success(auditService.getAuditLogs(page, size, targetType));
    }
}
