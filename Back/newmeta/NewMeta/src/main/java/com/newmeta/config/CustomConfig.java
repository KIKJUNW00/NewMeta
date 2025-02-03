package com.newmeta.config; // com.newmeta.config 패키지를 정의

import org.springframework.context.annotation.Bean; // Spring에서 Bean을 정의하기 위한 어노테이션 임포트
import org.springframework.context.annotation.Configuration; // Spring의 설정 클래스를 정의하기 위한 어노테이션 임포트
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCrypt 방식의 비밀번호 암호화를 위한 클래스 임포트
import org.springframework.security.crypto.password.PasswordEncoder; // 비밀번호 인코더 인터페이스 임포트

@Configuration // 이 클래스가 Spring의 설정 클래스임을 나타냄
public class CustomConfig {

    // BCrypt 암호화를 사용하여 비밀번호를 암호화하는 Bean을 정의
    @Bean // 이 메서드가 Spring의 Bean으로 등록될 것임을 나타냄
    PasswordEncoder passwordEncoder() { // PasswordEncoder 타입의 Bean을 반환하는 메서드
        return new BCryptPasswordEncoder(); // BCryptPasswordEncoder의 인스턴스를 생성하여 반환
    }

}
