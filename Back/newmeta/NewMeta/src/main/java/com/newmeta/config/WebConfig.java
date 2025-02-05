package com.newmeta.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 요청 허용
                .allowedOriginPatterns("*") // 모든 도메인 허용 (allowedOrigins("*") 대신 사용)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        // 🔹 WebSocket 요청에 대한 CORS 허용
        registry.addMapping("/ws-stomp/**") // WebSocket STOMP 경로 허용
                .allowedOriginPatterns("*") // 모든 프론트엔드 도메인에서의 요청 허용
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(true);
    }
}
