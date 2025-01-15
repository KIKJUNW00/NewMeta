package com.newmeta.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.newmeta.config.filter.JWTAuthenticationFilter;
import com.newmeta.config.filter.JWTAuthorizationFilter;
import com.newmeta.persistence.MemberRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig { // 보안 설정 파일
	
	// 로그인에 성공하면 임의의 사용자를 생성해서 DB에 저장하고 JWT 토큰을 만들어서 응답헤더에 설정하는 핸들러

	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;

	@Autowired
	private MemberRepository memRepo;

	

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(security -> security.requestMatchers("/member/**")
				.authenticated() // 로그인을 한(인증을 받은)
				.requestMatchers("/manager/**")
				.hasAnyRole("MANAGER", "ADMIN")
				.requestMatchers("/admin/**")
				.hasRole("ADMIN").anyRequest().permitAll());

		http.csrf(cf -> cf.disable()); // csrf 설정 무효화
		http.cors().and();
		
		// 기본 로그인 기능을 사용
		http.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/loginSuccess", false));
		http.exceptionHandling(ex -> ex.accessDeniedPage("/accessDenied"));

		http.logout(logout -> logout.invalidateHttpSession(true) // 현재 브라우저와 연결된 세션 강제 종료
				.deleteCookies("JSESSIONID") // 세션 아이디가 저장된 쿠키 삭제
				.logoutSuccessUrl("/login")); // 로그아웃 성공시 로그인 페이지로 이동

		// HttpBasic인증방식을사용하지않겠다는설정
		http.httpBasic(basic -> basic.disable());

		// 세션을유지하지않겠다고설정
		// Url 호출 뒤 응답할 때 까지는 유지되지만 응답 후 삭제된다는 의미
		 http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		// 스프링 시큐리티가 등록한 필터체인의 뒤에 작성한 필터를 추가한다
		http.addFilter(new JWTAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()));

		// 스프링 시큐리티가 등록한 필터들 중에서 AuthorizationFilter 앞에 앞에서 작성한 필터를 삽입한다.
		http.addFilterBefore(new JWTAuthorizationFilter(memRepo), AuthorizationFilter.class);
		

		return http.build();
	}

}
