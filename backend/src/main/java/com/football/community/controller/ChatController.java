package com.football.community.controller;

import com.football.community.dto.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.entity.ChatMessage;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "聊天管理", description = "聊天记录和管理接口")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "获取聊天列表", description = "获取当前用户有聊天记录的用户列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getChatList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "50") int limit) {
        return Result.success(chatService.getChatList(userDetails.getId(), limit));
    }

    @Operation(summary = "获取聊天历史", description = "分页获取与指定用户的聊天记录")
    @Parameters({
            @Parameter(name = "otherUserId", description = "对方用户ID", required = true),
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "20")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/history/{otherUserId}")
    public Result<IPage<ChatMessage>> getChatHistory(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(chatService.getChatHistory(userDetails.getId(), otherUserId, page, size));
    }

    @Operation(summary = "标记消息已读", description = "将来自指定用户的消息标记为已读")
    @Parameter(name = "senderId", description = "发送者用户ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/read/{senderId}")
    public Result<?> markAsRead(
            @PathVariable Long senderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.markAsRead(senderId, userDetails.getId());
        return Result.success();
    }

    @Operation(summary = "获取未读消息数", description = "获取指定用户发给当前用户的未读消息数量")
    @Parameters({
            @Parameter(name = "userId", description = "当前用户ID", required = true),
            @Parameter(name = "senderId", description = "发送者用户ID", required = true)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/unread/{userId}")
    public Result<Integer> getUnreadCount(
            @PathVariable Long userId,
            @RequestParam Long senderId) {
        return Result.success(chatService.getUnreadCount(userId, senderId));
    }
}
