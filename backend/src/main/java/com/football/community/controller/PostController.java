package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Post;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.PostService;
import com.football.community.service.LikeService;
import com.football.community.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public Result<IPage<Post>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category) {
        return Result.success(postService.getPostList(page, size, keyword, category));
    }

    @GetMapping("/{id}")
    public Result<Map<String, Object>> getPost(@PathVariable Long id,
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
    public Result<Post> createPost(@RequestBody Post post,
                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        post.setUserId(userDetails.getId());
        return Result.success(postService.createPost(post));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('post:edit')")
    public Result<Post> updatePost(@PathVariable Long id, @RequestBody Post post) {
        return Result.success(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('post:delete')")
    public Result<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return Result.success();
    }

    @PostMapping("/{id}/like")
    public Result<Map<String, Object>> likePost(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean liked = likeService.toggleLike(userDetails.getId(), id, 1);
        long count = likeService.countByTarget(id, 1);

        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("count", count);
        return Result.success(data);
    }

    @PostMapping("/{id}/favorite")
    public Result<Map<String, Object>> favoritePost(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean favorited = favoriteService.toggleFavorite(userDetails.getId(), id, 1);
        long count = favoriteService.countByTarget(id, 1);

        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        data.put("count", count);
        return Result.success(data);
    }
}
