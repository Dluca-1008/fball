package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.ChatMessage;

import java.util.List;
import java.util.Map;

/**
 * 聊天服务接口。
 * <p>提供聊天消息保存、历史记录查询、已读标记等业务逻辑。</p>
 */
public interface ChatService extends IService<ChatMessage> {

    /**
     * 保存聊天消息。
     * @param message 消息实体
     * @return 保存后的消息
     */
    ChatMessage saveMessage(ChatMessage message);

    /**
     * 分页获取两人之间的聊天历史。
     * @param userId1 用户1 ID
     * @param userId2 用户2 ID
     * @param page 页码
     * @param size 每页大小
     * @return 聊天历史分页结果
     */
    IPage<ChatMessage> getChatHistory(Long userId1, Long userId2, int page, int size);

    /**
     * 获取有聊天记录的用户列表。
     * @param userId 用户ID
     * @return 聊天用户列表
     */
    List<Map<String, Object>> getChatList(Long userId);

    /**
     * 将来自发送者的消息标记为已读。
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     */
    void markAsRead(Long senderId, Long receiverId);

    /**
     * 获取指定用户发给当前用户的未读消息数量。
     * @param userId 当前用户ID
     * @param senderId 发送者ID
     * @return 未读消息数量
     */
    int getUnreadCount(Long userId, Long senderId);
}
