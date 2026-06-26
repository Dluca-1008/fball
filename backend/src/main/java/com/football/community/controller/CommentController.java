package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Comment;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result<IPage<Comment>> getComments(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(commentService.getCommentsByPostId(postId, page, size));
    }

    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        comment.setUserId(userDetails.getId());
        return Result.success(commentService.createComment(comment));
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteComment(@PathVariable Long id,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getId());
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<?> likeComment(@PathVariable Long id) {
        commentService.likeComment(id);
        return Result.success();
    }
}
