package com.football.community.controller;

import com.football.community.dto.Result;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "点赞管理", description = "点赞相关接口")
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Operation(summary = "切换点赞状态", description = "对指定目标进行点赞/取消点赞操作")
    @Parameters({
            @Parameter(name = "targetId", description = "目标ID", required = true),
            @Parameter(name = "targetType", description = "目标类型(1-帖子,2-评论)", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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

    @Operation(summary = "检查点赞状态", description = "检查当前用户是否已点赞指定目标")
    @Parameters({
            @Parameter(name = "targetId", description = "目标ID", required = true),
            @Parameter(name = "targetType", description = "目标类型(1-帖子,2-评论)", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/check")
    public Result<Boolean> checkLike(
            @RequestParam Long targetId,
            @RequestParam Integer targetType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(likeService.isLiked(userDetails.getId(), targetId, targetType));
    }
}
