package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.ChatMessage;

import java.util.List;
import java.util.Map;

public interface ChatService extends IService<ChatMessage> {

    ChatMessage saveMessage(ChatMessage message);

    IPage<ChatMessage> getChatHistory(Long userId1, Long userId2, int page, int size);

    List<Map<String, Object>> getChatList(Long userId);

    void markAsRead(Long senderId, Long receiverId);

    int getUnreadCount(Long userId, Long senderId);
}
