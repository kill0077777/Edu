package com.edu.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.edu.entity.Member;
import com.edu.member.MemberRepository;
import com.edu.member.MemberUserDetails;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public CustomAuthenticationProvider(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

    @Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userId = authentication.getName();
		String rawPassword = authentication.getCredentials().toString();

		Member member = memberRepository.findByUserId(userId)
				.orElseThrow(() -> new BadCredentialsException("아이디가 존재하지 않습니다."));

		// === 여기에서 비밀번호 비교 및 로그 찍기! ===
		System.out.println("=== 여기에서 비밀번호 비교 및 로그 찍기! ===");
		System.out.println("DB 비번: " + member.getPassword());
		System.out.println("입력 비번: " + rawPassword);
		System.out.println("매치결과: " + passwordEncoder.matches(rawPassword, member.getPassword()));

		if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}

		// 권한 부여
		// String role = member.getRole(); // 이미 String이면
		// SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

		// 권한 부여
		// 여기가 핵심!
		//SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole()); // String이면 바로
		
		// 권한 부여
		// 만약 Enum이라면
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(member.getRole().getAuthority());

		// ✅ 여기에서 MemberUserDetails를 principal로 전달!
		// ✅ 반드시 principal에 MemberUserDetails 객체를 넣는다!
	    MemberUserDetails userDetails = new MemberUserDetails(member);

		return new UsernamePasswordAuthenticationToken(
			userDetails,			// principal: MemberUserDetails 객체
			null,					// credentials (보안상 null 추천)
			Collections.singleton(authority)
		);
	}

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}