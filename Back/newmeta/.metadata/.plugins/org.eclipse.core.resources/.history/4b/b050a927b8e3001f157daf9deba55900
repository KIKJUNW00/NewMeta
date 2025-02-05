package com.newmeta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // ✅ WebSocket STOMP 엔드포인트 설정
                .setAllowedOriginPatterns("*") // ✅ 모든 도메인에서의 요청 허용
                .withSockJS(); // ✅ SockJS 지원 (프론트엔드에서 WebSocket 대체 가능)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // ✅ 메시지 브로커 활성화
        registry.setApplicationDestinationPrefixes("/app"); // ✅ 클라이언트가 메시지를 보낼 프리픽스 설정
    }
}
