package com.newmeta.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.newmeta.domain.Admin;
import com.newmeta.persistence.AdminRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SecurityUserDetailsService implements UserDetailsService{
	
	private final  AdminRepository memRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 사용자 이름(username)을 입력받아 해당 사용자의 정보를 반환
		Admin admin = memRepo.findById(username) // username(Primary Key)으로 사용자를 조회
								.orElseThrow(()->new UsernameNotFoundException("Not Found!"));
		
		return new User(
				admin.getUsername(), // 데이터베이스에서 가져온 username을 설정
				admin.getPassword(), // 데이터베이스에서 가져온 암호화된 비밀번호를 설정
				AuthorityUtils.createAuthorityList(admin.getRole().toString())); // 사용자의 권한(Role)을 GrantedAuthority 객체 리스트로 변환
	}
}
