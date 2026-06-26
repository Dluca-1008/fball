package com.football.community.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MatchHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Long, Set<WebSocketSession>> matchSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long matchId = getMatchIdFromSession(session);
        if (matchId != null) {
            matchSessions.computeIfAbsent(matchId, k -> ConcurrentHashMap.newKeySet()).add(session);
            log.info("新客户端连接到赛事 {} 的实时推送", matchId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 客户端可以发送订阅/取消订阅消息
        log.debug("收到客户端消息: {}", message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long matchId = getMatchIdFromSession(session);
        if (matchId != null) {
            Set<WebSocketSession> sessions = matchSessions.get(matchId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    matchSessions.remove(matchId);
                }
            }
        }
        log.info("客户端断开连接");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Long matchId = getMatchIdFromSession(session);
        if (matchId != null) {
            Set<WebSocketSession> sessions = matchSessions.get(matchId);
            if (sessions != null) {
                sessions.remove(session);
            }
        }
        log.error("WebSocket传输错误", exception);
    }

    public void broadcastToMatch(Long matchId, Object message) {
        Set<WebSocketSession> sessions = matchSessions.get(matchId);
        if (sessions != null) {
            String jsonMessage;
            try {
                jsonMessage = objectMapper.writeValueAsString(message);
            } catch (Exception e) {
                log.error("序列化消息失败", e);
                return;
            }

            sessions.forEach(session -> {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(jsonMessage));
                    } catch (IOException e) {
                        log.error("发送消息失败", e);
                    }
                }
            });
        }
    }

    private Long getMatchIdFromSession(WebSocketSession session) {
        try {
            String path = session.getUri().getPath();
            String[] parts = path.split("/");
            if (parts.length > 0) {
                return Long.parseLong(parts[parts.length - 1]);
            }
        } catch (Exception e) {
            log.error("获取赛事ID失败", e);
        }
        return null;
    }
}
