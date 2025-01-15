//package com.newmeta.controller;
//
//@RestController // 모든 메서드가 JSON 또는 HTTP 응답으로 반환
//@RequiredArgsConstructor // final로 선언된 멤버 변수의 생성자를 자동으로 생성
//public class LoginController {
//	
//	@Autowired
//    private final MemberRegistrationService memberRegistrationService;
//    
//    @PostMapping("/signup")
//	public Member registerMember(@RequestBody Member member){
//		return memberRegistrationService.registerMember(member);
//	}
//}