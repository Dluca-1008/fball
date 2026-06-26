package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggleLike(
            @RequestParam Long targetId,
            @RequestParam Integer targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean liked = likeService.toggleLike(userDetails.getId(), targetId, targetType);
        long count = likeService.countByTarget(targetId, targetType);

        Map<String, Object> data = new HashMap<>();
        data.put("liked", liked);
        data.put("count", count);
        return Result.success(data);
    }

    @GetMapping("/check")
    public Result<Boolean> checkLike(
            @RequestParam Long targetId,
            @RequestParam Integer targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(likeService.isLiked(userDetails.getId(), targetId, targetType));
    }
}
