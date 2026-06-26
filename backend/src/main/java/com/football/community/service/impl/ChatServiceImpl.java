package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.entity.ChatMessage;
import com.football.community.entity.User;
import com.football.community.repository.ChatMessageMapper;
import com.football.community.service.ChatService;
import com.football.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        message.setStatus(0);
        message.setCreatedAt(LocalDateTime.now());
        save(message);

        fillUserInfo(message);
        return message;
    }

    @Override
    public IPage<ChatMessage> getChatHistory(Long userId1, Long userId2, int page, int size) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.and(w2 -> w2.eq(ChatMessage::getSenderId, userId1).eq(ChatMessage::getReceiverId, userId2))
                        .or(w2 -> w2.eq(ChatMessage::getSenderId, userId2).eq(ChatMessage::getReceiverId, userId1)));
        wrapper.orderByDesc(ChatMessage::getCreatedAt);

        IPage<ChatMessage> messagePage = page(new Page<>(page, size), wrapper);
        messagePage.getRecords().forEach(this::fillUserInfo);

        return messagePage;
    }

    @Override
    public List<Map<String, Object>> getChatList(Long userId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ChatMessage::getSenderId, userId).or().eq(ChatMessage::getReceiverId, userId));
        wrapper.orderByDesc(ChatMessage::getCreatedAt);

        List<ChatMessage> messages = list(wrapper);

        Map<Long, ChatMessage> latestMessages = new LinkedHashMap<>();
        for (ChatMessage message : messages) {
            Long otherUserId = message.getSenderId().equals(userId) ? message.getReceiverId() : message.getSenderId();
            if (!latestMessages.containsKey(otherUserId)) {
                fillUserInfo(message);
                latestMessages.put(otherUserId, message);
            }
        }

        return latestMessages.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> chat = new HashMap<>();
                    chat.put("userId", entry.getKey());
                    chat.put("lastMessage", entry.getValue());
                    chat.put("unreadCount", getUnreadCount(userId, entry.getKey()));
                    return chat;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long senderId, Long receiverId) {
        LambdaUpdateWrapper<ChatMessage> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ChatMessage::getSenderId, senderId)
               .eq(ChatMessage::getReceiverId, receiverId)
               .eq(ChatMessage::getStatus, 0)
               .set(ChatMessage::getStatus, 1);
        update(wrapper);
    }

    @Override
    public int getUnreadCount(Long userId, Long senderId) {
        LambdaQueryWrapper<ChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChatMessage::getSenderId, senderId)
               .eq(ChatMessage::getReceiverId, userId)
               .eq(ChatMessage::getStatus, 0);
        return (int) count(wrapper);
    }

    private void fillUserInfo(ChatMessage message) {
        if (message.getSenderId() != null) {
            User user = userService.getById(message.getSenderId());
            if (user != null) {
                message.setSenderName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                message.setSenderAvatar(user.getAvatar());
            }
        }
    }
}
