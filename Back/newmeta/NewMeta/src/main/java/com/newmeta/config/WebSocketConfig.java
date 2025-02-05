package com.newmeta.config; // 📌 WebSocket 설정 클래스가 위치하는 패키지

import org.springframework.context.annotation.Configuration; // 스프링 설정 클래스로 지정하는 어노테이션
import org.springframework.messaging.simp.config.MessageBrokerRegistry; // STOMP 메시지 브로커 설정 클래스
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker; // WebSocket 메시지 브로커 활성화 어노테이션
import org.springframework.web.socket.config.annotation.StompEndpointRegistry; // STOMP 엔드포인트 설정 클래스
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer; // WebSocket 및 STOMP 설정 인터페이스

/**
 * 📌 WebSocket 및 STOMP 설정 클래스
 * ✅ WebSocket을 활용하여 클라이언트와 서버 간의 실시간 통신을 가능하게 함
 */
@Configuration // ✅ Spring 설정 파일로 등록
@EnableWebSocketMessageBroker // ✅ WebSocket 및 STOMP 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // ✅ WebSocket 설정을 위한 인터페이스 구현

    /**
     * 🚀 STOMP 엔드포인트 등록
     * ✅ 클라이언트(Web)에서 WebSocket 연결을 생성할 경로를 설정
     * ✅ SockJS를 지원하여 WebSocket을 사용할 수 없는 환경에서도 대체 가능
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // ✅ WebSocket STOMP 엔드포인트 설정
                .setAllowedOriginPatterns("*") // ✅ 모든 도메인(Origin)에서 요청 허용 (CORS 대응)
                .withSockJS(); // ✅ SockJS 지원 (WebSocket을 사용할 수 없는 환경에서도 작동)
    }

    /**
     * 🚀 메시지 브로커 설정
     * ✅ 메시지 라우팅을 위한 브로커 및 애플리케이션 프리픽스를 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue"); // ✅ 메시지 브로커 활성화 (구독 대상 프리픽스 설정)
        registry.setApplicationDestinationPrefixes("/app"); // ✅ 클라이언트가 서버로 메시지를 보낼 때 사용하는 프리픽스 설정
    }
}
