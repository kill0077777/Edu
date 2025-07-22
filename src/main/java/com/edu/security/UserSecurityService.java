package com.edu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.edu.entity.Member;
import com.edu.member.MemberRepository;
import com.edu.member.MemberUserDetails;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

	@Autowired
	private MemberRepository memberRepository;

	//@Autowired
	//private MemberUserDetails memberUserDetails;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을수 없습니다."));

        // ✅ 반드시 MemberUserDetails로 반환
        return new MemberUserDetails(member);
    }
}
