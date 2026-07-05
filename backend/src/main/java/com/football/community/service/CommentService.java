package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Comment;

/**
 * 评论服务接口。
 * <p>提供评论的创建、删除、点赞及分页查询等业务逻辑。</p>
 */
public interface CommentService extends IService<Comment> {

    /**
     * 按帖子ID分页获取评论列表。
     * @param postId 帖子ID
     * @param page 页码
     * @param size 每页大小
     * @return 评论分页结果
     */
    IPage<Comment> getCommentsByPostId(Long postId, int page, int size);

    /**
     * 创建新评论。
     * @param comment 评论实体
     * @return 创建后的评论
     */
    Comment createComment(Comment comment);

    /**
     * 删除评论（仅限作者本人）。
     * @param id 评论ID
     * @param userId 用户ID
     */
    void deleteComment(Long id, Long userId);

    /**
     * 对评论进行点赞。
     * @param id 评论ID
     */
    void likeComment(Long id);
}
