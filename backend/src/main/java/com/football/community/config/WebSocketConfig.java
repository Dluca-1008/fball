package com.football.community.config;

import com.football.community.security.JwtTokenProvider;
import com.football.community.websocket.ChatHandler;
import com.football.community.websocket.MatchHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatHandler chatHandler;

    @Autowired
    private MatchHandler matchHandler;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
                .setAllowedOrigins("*");

        registry.addHandler(matchHandler, "/ws/match/{matchId}")
                .setAllowedOrigins("*");
    }
}
