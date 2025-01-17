package com.newmeta.service;

//1) 클라이언트가 username과 password로 로그인 요청.
//2) 스프링 시큐리티가 loadUserByUsername 메서드를 호출.
//3) 이 메서드가 MemberRepository를 통해 사용자 정보를 조회.
//4) 조회한 정보를 UserDetails 객체로 반환.
//5) Spring Security는 반환된 객체를 사용해 비밀번호를 확인하고 권한을 검사

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.newmeta.domain.Member;
import com.newmeta.persistence.MemberRepository;


@Service
public class SecurityUserDetailsService implements UserDetailsService{
	
	@Autowired
	private MemberRepository memRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // 사용자 이름(username)을 입력받아 해당 사용자의 정보를 반환
		Member member = memRepo.findById(username) // username(Primary Key)으로 사용자를 조회
								.orElseThrow(()->new UsernameNotFoundException("Not Found!"));
		
		return new User(
				member.getUsername(), // 데이터베이스에서 가져온 username을 설정
				member.getPassword(), // 데이터베이스에서 가져온 암호화된 비밀번호를 설정
				AuthorityUtils.createAuthorityList(member.getRole().toString())); // 사용자의 권한(Role)을 GrantedAuthority 객체 리스트로 변환
	}
}
