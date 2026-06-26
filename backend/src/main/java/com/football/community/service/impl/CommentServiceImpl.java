package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.Comment;
import com.football.community.entity.Post;
import com.football.community.entity.User;
import com.football.community.exception.BusinessException;
import com.football.community.repository.CommentMapper;
import com.football.community.service.CommentService;
import com.football.community.service.PostService;
import com.football.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Override
    public IPage<Comment> getCommentsByPostId(Long postId, int page, int size) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPostId, postId)
               .eq(Comment::getStatus, 1)
               .orderByAsc(Comment::getCreatedAt);
        IPage<Comment> commentPage = page(new Page<>(page, size), wrapper);

        commentPage.getRecords().forEach(this::fillAuthorInfo);

        return commentPage;
    }

    @Override
    @Transactional
    public Comment createComment(Comment comment) {
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setCreatedAt(LocalDateTime.now());
        save(comment);

        Post post = postService.getById(comment.getPostId());
        if (post != null) {
            post.setCommentCount(post.getCommentCount() + 1);
            postService.updateById(post);
        }

        fillAuthorInfo(comment);
        return comment;
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long userId) {
        Comment comment = getById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("只能删除自己的评论");
        }

        removeById(id);

        Post post = postService.getById(comment.getPostId());
        if (post != null && post.getCommentCount() > 0) {
            post.setCommentCount(post.getCommentCount() - 1);
            postService.updateById(post);
        }
    }

    @Override
    public void likeComment(Long id) {
        LambdaUpdateWrapper<Comment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Comment::getId, id)
               .setSql("like_count = like_count + 1");
        update(wrapper);
    }

    private void fillAuthorInfo(Comment comment) {
        if (comment.getUserId() != null) {
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                comment.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                comment.setAuthorAvatar(user.getAvatar());
            }
        }
    }
}
