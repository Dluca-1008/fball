package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.entity.Comment;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论管理", description = "帖子评论相关接口")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "获取帖子评论列表", description = "分页获取指定帖子的评论列表")
    @Parameters({
            @Parameter(name = "postId", description = "帖子ID", required = true),
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<Comment>> getComments(
            @RequestParam Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(commentService.getCommentsByPostId(postId, page, size));
    }

    @Operation(summary = "创建评论", description = "对指定帖子发表评论")
    @Parameter(description = "评论内容")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "评论成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping
    public Result<Comment> createComment(@RequestBody Comment comment,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        comment.setUserId(userDetails.getId());
        return Result.success(commentService.createComment(comment));
    }

    @Operation(summary = "删除评论", description = "删除指定评论（仅限作者本人）")
    @Parameter(name = "id", description = "评论ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/{id}")
    public Result<?> deleteComment(@PathVariable Long id,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getId());
        return Result.success();
    }

    @Operation(summary = "点赞评论", description = "对指定评论进行点赞")
    @Parameter(name = "id", description = "评论ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "点赞成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{id}/like")
    public Result<?> likeComment(@PathVariable Long id) {
        commentService.likeComment(id);
        return Result.success();
    }
}
