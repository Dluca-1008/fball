package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.AuditLog;

/**
 * 内容审核服务接口。
 * <p>提供帖子和评论的审核相关业务逻辑。</p>
 */
public interface AuditService extends IService<AuditLog> {

    /**
     * 审核帖子。
     * @param postId 帖子ID
     * @param action 审核动作（1-通过, 2-拒绝）
     * @param reason 拒绝原因
     * @param operatorId 操作员ID
     */
    void auditPost(Long postId, Integer action, String reason, Long operatorId);

    /**
     * 审核评论。
     * @param commentId 评论ID
     * @param action 审核动作（1-通过, 2-拒绝）
     * @param reason 拒绝原因
     * @param operatorId 操作员ID
     */
    void auditComment(Long commentId, Integer action, String reason, Long operatorId);

    /**
     * 分页获取审核日志。
     * @param page 页码
     * @param size 每页大小
     * @param targetType 目标类型筛选
     * @return 审核日志分页结果
     */
    IPage<AuditLog> getAuditLogs(int page, int size, Integer targetType);
}
