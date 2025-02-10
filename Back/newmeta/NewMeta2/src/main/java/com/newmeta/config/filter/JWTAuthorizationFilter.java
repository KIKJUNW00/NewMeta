package com.newmeta.config.filter; // 📌 해당 보안 필터가 속한 패키지

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus; // HTTP 상태 코드 관리
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Spring Security 인증 토큰
import org.springframework.security.core.authority.AuthorityUtils; // Spring Security 권한 관리
import org.springframework.security.core.context.SecurityContextHolder; // SecurityContext에 인증 정보 저장
import org.springframework.security.core.userdetails.User; // Spring Security 사용자 객체
import org.springframework.web.filter.OncePerRequestFilter; // 요청 당 한 번 실행되는 필터
import org.springframework.security.core.Authentication;

import com.auth0.jwt.JWT; // JWT 토큰 라이브러리
import com.auth0.jwt.algorithms.Algorithm; // JWT 서명 알고리즘
import com.newmeta.domain.Admin; // 관리자 계정 엔티티
import com.newmeta.persistence.AdminRepository; // 관리자 정보 조회 리포지토리

import jakarta.servlet.FilterChain; // 필터 체인 관리
import jakarta.servlet.ServletException; // Servlet 예외 처리 클래스
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 객체
import jakarta.servlet.http.HttpServletResponse; // HTTP 응답 객체

import lombok.RequiredArgsConstructor; // Lombok - final 필드 자동 생성자 주입
import lombok.extern.slf4j.Slf4j; // Lombok - 로그 기록

/**
 * 📌 JWT 기반 요청 인증 필터
 * ✅ 사용자의 JWT를 검증하여 인증 정보를 `SecurityContextHolder`에 저장하는 역할
 */
@Slf4j // ✅ 로그 기록을 위한 Lombok 어노테이션
@RequiredArgsConstructor // ✅ final 필드에 대한 생성자를 Lombok이 자동 생성
public class JWTAuthorizationFilter extends OncePerRequestFilter { // ✅ OncePerRequestFilter: 모든 요청마다 한 번 실행됨

    private final AdminRepository adminRepo; // ✅ Admin 정보를 조회할 JPA Repository (DB에서 사용자 정보 검증)

    /**
     * 🚀 JWT 검증 필터 실행 (모든 요청에서 실행됨)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ 현재 요청 URI 확인 (로그 출력)
        String requestURI = request.getRequestURI();
        log.info("🔍 [요청 URI] {}", requestURI);

        // ✅ WebSocket 관련 요청은 JWT 검증 제외
        if (requestURI.startsWith("/ws-stomp") || requestURI.startsWith("/scm")) {
            log.info("🔄 WebSocket 요청 감지 - JWT 검증 제외");
            filterChain.doFilter(request, response); // 다음 필터로 요청 전달
            return;
        }

        // ✅ HTTP 요청 헤더에서 JWT 토큰 추출
        String srcToken = request.getHeader("Authorization");
        if (srcToken == null || !srcToken.startsWith("Bearer ")) {
            log.warn("⛔ [JWT 필터] Authorization 헤더 없음 또는 형식 오류");
            filterChain.doFilter(request, response); // 다음 필터로 요청 전달
            return;
        }

        // ✅ "Bearer " 접두사 제거하여 순수 JWT 토큰 추출
        String jwtToken = srcToken.replace("Bearer ", "");
        log.info("🔑 [JWT 검증] 토큰 수신: {}", jwtToken);

        try {
            // ✅ JWT 토큰을 검증하고 `username` 클레임(Claim) 추출
            String username = JWT.require(Algorithm.HMAC256("com.newmeta.jwt")) // JWT 서명 알고리즘
                    .build()
                    .verify(jwtToken) // 토큰 검증 (예외 발생 시 인증 실패)
                    .getClaim("username") // JWT에서 `username` 정보 추출
                    .asString();

            log.info("✅ [JWT 검증 성공] username: {}", username);

            // ✅ 데이터베이스에서 사용자 정보 조회
            Optional<Admin> opt = adminRepo.findById(username);
            if (!opt.isPresent()) {
                log.warn("❌ [JWT 인증 실패] 존재하지 않는 사용자: {}", username);
                filterChain.doFilter(request, response); // 다음 필터로 요청 전달
                return;
            }

            // ✅ 인증된 사용자 정보 로드
            Admin findAdmin = opt.get();
            log.info("👤 [인증된 사용자] ID: {}, Role: {}", findAdmin.getUsername(), findAdmin.getRole());

            // ✅ Spring Security의 User 객체 생성
            User user = new User(findAdmin.getUsername(), findAdmin.getPassword(),
                    AuthorityUtils.createAuthorityList(findAdmin.getRole().toString())); // 사용자의 역할(Role) 등록

            // ✅ 인증 객체 생성 및 SecurityContextHolder에 저장
            Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response); // 다음 필터로 요청 전달
        } catch (Exception e) {
            log.error("🚨 [JWT 검증 오류] {}", e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 인증 실패 시 HTTP 401 응답
        }
    }
}
