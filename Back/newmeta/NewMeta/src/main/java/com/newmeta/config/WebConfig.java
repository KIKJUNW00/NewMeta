package com.newmeta.config; // 해당 설정 파일이 속한 패키지를 선언

import org.springframework.context.annotation.Configuration; // 스프링 설정 클래스를 선언하는 어노테이션
import org.springframework.web.servlet.config.annotation.CorsRegistry; // CORS 설정을 위한 클래스
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // 웹 MVC 설정 인터페이스

/**
 * 📌 Spring Boot CORS 설정 파일
 * ✅ 외부(프론트엔드)에서 API 및 WebSocket 접근을 허용하는 역할
 */
@Configuration // 스프링의 설정 파일임을 선언 (Spring이 자동으로 감지하여 적용)
public class WebConfig implements WebMvcConfigurer { // WebMvcConfigurer를 구현하여 CORS 설정을 커스터마이징

    /**
     * 🚀 CORS 정책 설정 (REST API 및 WebSocket을 위한 설정)
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // ✅ 모든 REST API 요청에 대한 CORS 허용
        registry.addMapping("/**") // 모든 엔드포인트 허용 (REST API 요청)
                .allowedOriginPatterns("*") // 모든 도메인(Origin)에서의 요청 허용 (allowedOrigins("*") 대신 사용)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 지정
                .allowedHeaders("*") // 모든 HTTP 헤더 허용
                .exposedHeaders("Authorization")  // Authorization 헤더 노출
                .allowCredentials(true); // 쿠키 및 인증 정보 포함 허용

        /**
         * 🚀 WebSocket(STOMP) 요청에 대한 CORS 허용
         * ✅ 프론트엔드에서 WebSocket 연결을 원활하게 수행할 수 있도록 설정
         */
        registry.addMapping("/ws-stomp/**") // WebSocket STOMP 경로 허용
                .allowedOriginPatterns("*") // 모든 프론트엔드 도메인에서의 요청 허용
                .allowedMethods("GET", "POST", "OPTIONS") // WebSocket에서는 GET, POST, OPTIONS 허용
                .allowCredentials(true); // WebSocket 요청에서도 인증 정보 포함 허용
    }
}
