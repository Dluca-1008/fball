package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.CreatePostRequest;
import com.football.community.dto.Result;
import com.football.community.entity.Post;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.PostService;
import com.football.community.service.LikeService;
import com.football.community.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "帖子管理", description = "帖子CRUD、点赞、收藏接口")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "获取帖子列表", description = "分页查询帖子，支持关键词和分类筛选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<IPage<Post>> getPosts(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "帖子分类") @RequestParam(required = false) String category) {
        return Result.success(postService.getPostList(page, size, keyword, category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取帖子详情", description = "根据ID获取帖子详情，自动增加浏览量，返回用户点赞和收藏状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Map<String, Object>> getPost(@Parameter(description = "帖子ID", example = "1") @PathVariable Long id,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.incrementViewCount(id);
        Post post = postService.getById(id);

        Map<String, Object> data = new HashMap<>();
        data.put("post", post);

        if (userDetails != null) {
            data.put("liked", likeService.isLiked(userDetails.getId(), id, 1));
            data.put("favorited", favoriteService.isFavorited(userDetails.getId(), id, 1));
        }

        return Result.success(data);
    }

    @PostMapping
    @Operation(summary = "创建帖子", description = "创建新帖子")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Post> createPost(@Valid @RequestBody CreatePostRequest request,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setUserId(userDetails.getId());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        return Result.success(postService.createPost(post));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('post:edit')")
    @Operation(summary = "更新帖子", description = "根据ID更新帖子内容")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Post> updatePost(@Parameter(description = "帖子ID", example = "1") @PathVariable Long id, @Parameter(description = "帖子信息") @RequestBody Post post) {
        return Result.success(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('post:delete')")
    @Operation(summary = "删除帖子", description = "根据ID删除帖子")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<?> deletePost(@Parameter(description = "帖子ID", example = "1") @PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞帖子", description = "切换用户对帖子的点赞状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Map<String, Object>> likePost(@Parameter(description = "帖子ID", example = "1") @PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean liked = likeService.toggleLike(userDetails.getId(), id, 1);
        long count = likeService.countByTarget(id, 1);

        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("count", count);
        return Result.success(data);
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏帖子", description = "切换用户对帖子的收藏状态")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Result<Map<String, Object>> favoritePost(@Parameter(description = "帖子ID", example = "1") @PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean favorited = favoriteService.toggleFavorite(userDetails.getId(), id, 1);
        long count = favoriteService.countByTarget(id, 1);

        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        data.put("count", count);
        return Result.success(data);
    }
}
