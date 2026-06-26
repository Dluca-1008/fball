package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggleFavorite(
            @RequestParam Long targetId,
            @RequestParam Integer targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean favorited = favoriteService.toggleFavorite(userDetails.getId(), targetId, targetType);
        long count = favoriteService.countByTarget(targetId, targetType);

        Map<String, Object> data = new HashMap<>();
        data.put("favorited", favorited);
        data.put("count", count);
        return Result.success(data);
    }

    @GetMapping("/check")
    public Result<Boolean> checkFavorite(
            @RequestParam Long targetId,
            @RequestParam Integer targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(favoriteService.isFavorited(userDetails.getId(), targetId, targetType));
    }
}
