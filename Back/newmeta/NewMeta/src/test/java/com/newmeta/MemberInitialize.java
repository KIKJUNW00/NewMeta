package com.newmeta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.newmeta.domain.Member;
import com.newmeta.domain.Role;
import com.newmeta.persistence.MemberRepository;

@SpringBootTest
public class MemberInitialize {

	@Autowired
	MemberRepository memRepo;
	PasswordEncoder encoder = new BCryptPasswordEncoder();

	@Test
	public void doWork() {
		memRepo.save(Member.builder()
			.username("admin") // id 설정
			.password(encoder.encode("abcd")) // 비밀번호 설정 
			.role(Role.ROLE_ADMIN) // 권한 설정
			.build());
		
		memRepo.save(Member.builder()
				.username("test") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build()); 
		
	}
}