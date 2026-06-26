package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.Comment;

public interface CommentService extends IService<Comment> {

    IPage<Comment> getCommentsByPostId(Long postId, int page, int size);

    Comment createComment(Comment comment);

    void deleteComment(Long id, Long userId);

    void likeComment(Long id);
}
