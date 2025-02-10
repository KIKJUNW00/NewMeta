package com.newmeta.config; // 패키지 선언: com.newmeta.config 패키지에 속하는 클래스

import org.springframework.context.annotation.Bean; // Bean을 정의하기 위한 어노테이션 임포트
import org.springframework.context.annotation.Configuration; // 설정 클래스를 정의하기 위한 어노테이션 임포트
import org.springframework.http.HttpHeaders; // HTTP 헤더 처리를 위한 클래스 임포트
import org.springframework.http.MediaType; // 미디어 타입 처리를 위한 클래스 임포트
import org.springframework.web.reactive.function.client.WebClient; // 비동기 웹 클라이언트를 위한 클래스 임포트

@Configuration // 이 클래스가 Spring의 설정 클래스임을 나타내는 어노테이션
public class WebClientConfig { // WebClientConfig 클래스 정의

    @Bean // 이 메서드가 Spring의 Bean 정의임을 나타내는 어노테이션
    public WebClient webClient(WebClient.Builder builder) { // WebClient 인스턴스를 생성하는 메서드
        return builder // WebClient.Builder를 사용하여 WebClient 인스턴스 생성
                .baseUrl("http://10.125.121.116:8000") // 기본 URL 설정
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 기본 헤더 설정 (Content-Type: application/json)
                .build(); // WebClient 인스턴스 빌드 및 반환
    }
}
//package com.newmeta.config; // 패키지 선언: com.newmeta.config 패키지에 속하는 클래스
//
//import org.springframework.context.annotation.Bean; // Bean을 정의하기 위한 어노테이션 임포트
//import org.springframework.context.annotation.Configuration; // 설정 클래스를 정의하기 위한 어노테이션 임포트
//import org.springframework.http.HttpHeaders; // HTTP 헤더 처리를 위한 클래스 임포트
//import org.springframework.http.MediaType; // 미디어 타입 처리를 위한 클래스 임포트
//import org.springframework.web.reactive.function.client.WebClient; // 비동기 웹 클라이언트를 위한 클래스 임포트
//
//@Configuration // 이 클래스가 Spring의 설정 클래스임을 나타내는 어노테이션
//public class WebClientConfig { // WebClientConfig 클래스 정의
//
//    @Bean // 이 메서드가 Spring의 Bean 정의임을 나타내는 어노테이션
//    public WebClient webClient(WebClient.Builder builder) { // WebClient 인스턴스를 생성하는 메서드
//        return builder // WebClient.Builder를 사용하여 WebClient 인스턴스 생성
//                .baseUrl("http://localhost:8000") // 기본 URL 설정
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // 기본 헤더 설정 (Content-Type: application/json)
//                .build(); // WebClient 인스턴스 빌드 및 반환
//    }
//}
