package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.AuditLog;

public interface AuditService extends IService<AuditLog> {

    void auditPost(Long postId, Integer action, String reason, Long operatorId);

    void auditComment(Long commentId, Integer action, String reason, Long operatorId);

    IPage<AuditLog> getAuditLogs(int page, int size, Integer targetType);
}
