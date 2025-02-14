package com.newmeta.config; // 패키지 선언: com.newmeta.config 패키지 내에서 관리

// ✅ Spring Security 관련 라이브러리 임포트
import org.springframework.beans.factory.annotation.Autowired; // 스프링 빈을 주입하기 위한 어노테이션
import org.springframework.context.annotation.Bean; // 스프링 빈을 등록하는 어노테이션
import org.springframework.context.annotation.Configuration; // 설정 클래스를 선언하는 어노테이션
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // 인증 관련 설정 클래스
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // HTTP 보안 설정을 위한 빌더 클래스
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // 웹 보안 활성화 어노테이션
import org.springframework.security.config.http.SessionCreationPolicy; // 세션 관리 정책을 설정하는 클래스
import org.springframework.security.web.SecurityFilterChain; // 보안 필터 체인을 관리하는 클래스
import org.springframework.security.web.access.intercept.AuthorizationFilter; // 인가 필터 관련 클래스

// ✅ JWT 인증 및 인가 필터 임포트
import com.newmeta.config.filter.JWTAuthenticationFilter; // JWT 기반 인증 필터
import com.newmeta.config.filter.JWTAuthorizationFilter; // JWT 기반 인가 필터
import com.newmeta.persistence.AdminRepository; // 관리자 계정 정보를 관리하는 JPA 리포지토리

import lombok.RequiredArgsConstructor; // final 필드에 대한 생성자를 Lombok이 자동 생성

/**
 * 📌 Spring Security 설정 파일
 * ✅ JWT 기반 인증 및 인가를 관리하는 보안 설정 클래스
 */
@RequiredArgsConstructor // Lombok이 자동으로 final 필드에 대한 생성자를 생성
@Configuration // Spring 설정 클래스로 선언
@EnableWebSecurity // Spring Security를 활성화
public class SecurityConfig { // 보안 설정 클래스

    // ✅ Spring Security의 인증 설정을 관리하는 객체 (자동 주입)
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    // ✅ 관리자 계정 정보를 조회할 리포지토리 (JWT 검증 시 필요)
    @Autowired
    private AdminRepository adminRepo;

    /**
     * 🚀 Spring Security의 SecurityFilterChain 설정 (보안 정책을 적용하는 핵심 메서드)
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ✅ HTTP 요청에 대한 보안 설정
        http.authorizeHttpRequests(security -> security
                .requestMatchers("/ws/**", "/ws-stomp/**").permitAll() // ✅ WebSocket 요청은 인증 없이 허용
                .requestMatchers("/scm/data", "/scm/anomalies","/scm/hub-wise-data").permitAll() // ✅ 특정 API 엔드포인트 인증 없이 허용
                .requestMatchers("/community/**").permitAll()
                .requestMatchers("/login").permitAll() // ✅ 로그인 경로 허용
                .requestMatchers("/scm/**").authenticated() // ✅ `/scm/` 경로는 인증 필요
                .requestMatchers("/member/**").authenticated() // ✅ `/member/` 경로는 로그인한 사용자만 접근 가능
                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN") // ✅ 매니저 및 관리자만 접근 가능
                .requestMatchers("/admin/**").permitAll() // ✅ 관리자만 접근 가능
                .anyRequest().permitAll() // ✅ 나머지 모든 요청은 인증 없이 접근 허용
        );

        /**
         * 🚀 CSRF 설정 (JWT 사용 시 비활성화)
         * ✅ JWT는 Stateless(무상태)이므로 CSRF 토큰이 필요하지 않음
         */
        http.csrf(csrf -> csrf.disable());

        /**
         * 🚀 CORS 설정 (Cross-Origin Resource Sharing 허용)
         * ✅ 클라이언트(React 등)에서 서버 API를 호출할 수 있도록 설정
         */
        http.cors().and();
        /**
         * 🚀 접근 거부 시 이동할 페이지 설정
         * ✅ 사용자가 접근 권한이 없는 페이지 요청 시 `/accessDenied`로 리다이렉트
         */
        http.exceptionHandling(ex -> ex.accessDeniedPage("/accessDenied"));

        /**
         * 🚀 로그아웃 설정
         * ✅ 로그아웃 시 세션 삭제 및 쿠키 제거 후 로그인 페이지로 이동
         */
        http.logout(logout -> logout.invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login"));

        /**
         * 🚀 HTTP 기본 인증 사용 안 함
         * ✅ Spring Security의 기본 인증을 비활성화하고, JWT 인증만 사용
         */
        http.httpBasic(basic -> basic.disable());

        /**
         * 🚀 세션을 사용하지 않음 (JWT 사용을 위해 필요)
         * ✅ JWT는 상태 정보를 서버가 유지하지 않으므로 세션을 사용하지 않도록 설정
         */
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /**
         * 🚀 JWT 인증 필터 추가
         * ✅ 로그인 요청을 가로채어 JWT를 생성하는 필터
         */
        http.addFilter(new JWTAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()));

        /**
         * 🚀 JWT 인가 필터 추가 (WebSocket 요청은 제외)
         * ✅ 요청 헤더에서 JWT를 검증하여 사용자 인증 수행
         */
        http.addFilterBefore(new JWTAuthorizationFilter(adminRepo), AuthorizationFilter.class);

        return http.build(); // 최종 설정을 적용한 SecurityFilterChain 반환
    }
}
