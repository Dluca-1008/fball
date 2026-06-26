package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.ChatMessage;
import com.football.community.security.CustomUserDetails;
import com.football.community.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getChatList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(chatService.getChatList(userDetails.getId()));
    }

    @GetMapping("/history/{otherUserId}")
    public Result<IPage<ChatMessage>> getChatHistory(
            @PathVariable Long otherUserId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return Result.success(chatService.getChatHistory(userDetails.getId(), otherUserId, page, size));
    }

    @PostMapping("/read/{senderId}")
    public Result<?> markAsRead(
            @PathVariable Long senderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        chatService.markAsRead(senderId, userDetails.getId());
        return Result.success();
    }

    @GetMapping("/unread/{userId}")
    public Result<Integer> getUnreadCount(
            @PathVariable Long userId,
            @RequestParam Long senderId) {
        return Result.success(chatService.getUnreadCount(userId, senderId));
    }
}
