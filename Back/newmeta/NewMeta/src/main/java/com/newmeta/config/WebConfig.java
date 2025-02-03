package com.newmeta.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	
		
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 모든 경로에 대한 cors 설정 적용
				.allowedOriginPatterns("*") // 모든 도메인에서의 요청 허용
				.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 모든 HTTP 메소드 허용
				.allowedHeaders("*") // 모든 헤더를 허용
				.allowCredentials(true) // 쿠키와 같은 자격증명을 포함한 요청 허용
				.exposedHeaders("CorsConfiguration.ALL"); // 클라이언트가 접근할 수 있는 응답 헤더
		
		// 🔹 WebSocket CORS 설정 추가
       
		registry.addMapping("/ws/**") // WebSocket 요청 허용
                .allowedOrigins("http://localhost:3000") // WebSocket 요청 허용
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowCredentials(true);
        
        
        		
	}


}
