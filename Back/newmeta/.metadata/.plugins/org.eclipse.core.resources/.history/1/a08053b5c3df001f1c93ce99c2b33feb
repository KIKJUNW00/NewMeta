package com.newmeta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.newmeta.domain.Admin;
import com.newmeta.domain.Role;
import com.newmeta.persistence.AdminRepository;

@SpringBootTest
public class MemberInitialize {

	@Autowired
	AdminRepository adminRepo;
	PasswordEncoder encoder = new BCryptPasswordEncoder();

	@Test
	public void doWork() {
		adminRepo.save(Admin.builder()
			.username("admin1") // id 설정
			.password(encoder.encode("abcd")) // 비밀번호 설정 
			.role(Role.ROLE_ADMIN) // 권한 설정
			.build());
		adminRepo.save(Admin.builder()
				.username("admin2") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin3") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin4") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin5") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin6") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin7") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin8") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin9") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		adminRepo.save(Admin.builder()
				.username("admin10") // id 설정
				.password(encoder.encode("abcd")) // 비밀번호 설정 
				.role(Role.ROLE_ADMIN) // 권한 설정
				.build());
		
		
	}
}