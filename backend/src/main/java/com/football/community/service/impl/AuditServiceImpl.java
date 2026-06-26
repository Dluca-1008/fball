package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.AuditLog;
import com.football.community.entity.Comment;
import com.football.community.entity.Post;
import com.football.community.repository.AuditLogMapper;
import com.football.community.service.AuditService;
import com.football.community.service.CommentService;
import com.football.community.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog> implements AuditService {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Override
    @Transactional
    public void auditPost(Long postId, Integer action, String reason, Long operatorId) {
        Post post = postService.getById(postId);
        if (post == null) {
            throw new RuntimeException("帖子不存在");
        }

        post.setStatus(action == 1 ? 1 : 0);
        postService.updateById(post);

        AuditLog auditLog = new AuditLog();
        auditLog.setTargetId(postId);
        auditLog.setTargetType(1);
        auditLog.setUserId(post.getUserId());
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setOperatorId(operatorId);
        auditLog.setCreatedAt(LocalDateTime.now());
        save(auditLog);
    }

    @Override
    @Transactional
    public void auditComment(Long commentId, Integer action, String reason, Long operatorId) {
        Comment comment = commentService.getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }

        comment.setStatus(action == 1 ? 1 : 0);
        commentService.updateById(comment);

        AuditLog auditLog = new AuditLog();
        auditLog.setTargetId(commentId);
        auditLog.setTargetType(2);
        auditLog.setUserId(comment.getUserId());
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setOperatorId(operatorId);
        auditLog.setCreatedAt(LocalDateTime.now());
        save(auditLog);
    }

    @Override
    public IPage<AuditLog> getAuditLogs(int page, int size, Integer targetType) {
        LambdaQueryWrapper<AuditLog> wrapper = new LambdaQueryWrapper<>();
        if (targetType != null) {
            wrapper.eq(AuditLog::getTargetType, targetType);
        }
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }
}
