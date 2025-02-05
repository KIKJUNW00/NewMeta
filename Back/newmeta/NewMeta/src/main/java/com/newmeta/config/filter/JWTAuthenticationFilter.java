package com.newmeta.config.filter; // 패키지 선언: com.newmeta.config.filter 내에서 관리

import java.io.IOException;
import java.util.Date;

import org.springframework.http.HttpHeaders; // HTTP 헤더 관련 클래스
import org.springframework.http.HttpStatus; // HTTP 상태 코드 관련 클래스
import org.springframework.security.authentication.AuthenticationManager; // Spring Security의 인증 관리 클래스
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 사용자명/비밀번호 기반 인증 토큰
import org.springframework.security.core.Authentication; // 인증 객체
import org.springframework.security.core.AuthenticationException; // 인증 과정에서 발생하는 예외 클래스
import org.springframework.security.core.userdetails.User; // Spring Security의 사용자 객체
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // 기본 로그인 필터

import com.auth0.jwt.JWT; // JWT 생성 및 검증을 위한 라이브러리
import com.auth0.jwt.algorithms.Algorithm; // JWT 서명 알고리즘
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 데이터를 Java 객체로 변환
import com.newmeta.domain.Admin; // 관리자 정보 엔티티 클래스

import jakarta.servlet.FilterChain; // 필터 체인 관리 클래스
import jakarta.servlet.ServletException; // Servlet 예외 클래스
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 객체
import jakarta.servlet.http.HttpServletResponse; // HTTP 응답 객체

import lombok.RequiredArgsConstructor; // Lombok: final 필드 생성자 자동 생성
import lombok.extern.slf4j.Slf4j; // Lombok: 로그 기록을 위한 어노테이션

/**
 * 📌 JWT 기반 로그인 인증 필터
 * ✅ 사용자가 로그인하면 ID/PW 검증 후 JWT 토큰을 생성하여 반환하는 역할
 */
@Slf4j // ✅ 로그 기록을 위한 Lombok 어노테이션
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자 자동 생성 (의존성 주입 간소화)
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager; // ✅ Spring Security의 인증 관리자

    /**
     * 🚀 로그인 요청이 들어오면 사용자 인증을 시도하는 메서드
     * - 사용자가 `POST /login` 요청 시 실행됨
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("🔍 [로그인 시도] JWTAuthenticationFilter 실행됨");

        // ✅ ObjectMapper를 사용하여 JSON 데이터를 Admin 객체로 변환
        ObjectMapper mapper = new ObjectMapper();
        try {
            // ✅ HTTP 요청에서 JSON 데이터를 읽어 Admin 객체로 변환
            Admin admin = mapper.readValue(request.getInputStream(), Admin.class);

            // ✅ 사용자명과 비밀번호를 기반으로 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(admin.getUsername(), admin.getPassword());

            // ✅ Spring Security의 AuthenticationManager를 통해 인증 수행
            return authenticationManager.authenticate(authToken);

        } catch (Exception e) {
            log.info("❌ [로그인 실패] {}", e.getMessage()); // 인증 실패 로그 출력
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 인증 실패 시 401 응답 코드 반환
        }

        return null; // 인증 실패 시 null 반환
    }

    /**
     * 🚀 로그인 성공 시 실행되는 메서드
     * - 사용자 정보를 기반으로 JWT를 생성하고 응답 헤더에 추가
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        // ✅ 인증 성공한 사용자 객체 가져오기
        User user = (User) authResult.getPrincipal();
        log.info("✅ [로그인 성공] 사용자: {}", user.getUsername());

        // ✅ JWT 토큰 생성 (유효기간 100분)
        String token = JWT.create()
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 100)) // ✅ 100분 후 만료
                .withClaim("username", user.getUsername()) // ✅ JWT에 사용자명 포함
                .sign(Algorithm.HMAC256("com.newmeta.jwt")); // ✅ HMAC256 알고리즘을 사용하여 서명

        // ✅ 응답 헤더에 JWT 추가
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        response.setStatus(HttpStatus.OK.value()); // ✅ 응답 상태 코드 200 (성공)
        response.getWriter().write(user.getUsername()); // ✅ 응답 바디에 사용자명 추가
    }
}
