package com.edu.member;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.edu.dto.MemberFormDto;
import com.edu.entity.Member;
import com.edu.entity.Role;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;


	// 회원가입 메소드
	public void registerMember(MemberFormDto memberFormDto) {
	    Member member = new Member();
	    member.setUserId(memberFormDto.getUserId());
	    member.setName(memberFormDto.getName());
	    member.setEmail(memberFormDto.getEmail());

	    // 반드시 암호화해서 저장
	    String encodedPassword = passwordEncoder.encode(memberFormDto.getPassword());
	    member.setPassword(encodedPassword);

	    memberRepository.save(member);
	}

	public Page<Member> findAll(Pageable pageable) {
        return memberRepository.findAll(pageable);
    }

    public Optional<Member> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }

    public MemberFormDto getFormDtoByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .map(MemberFormDto::fromEntity)
                .orElse(new MemberFormDto());
    }

    public boolean isUniqueUserId(String userId, Long memberId) {
        Optional<Member> member = memberRepository.findByUserId(userId);
        return member.isEmpty() || (memberId != null && member.get().getMemberId().equals(memberId));
    }
    public boolean isUniqueEmail(String email, Long memberId) {
        Optional<Member> member = memberRepository.findByEmail(email);
        return member.isEmpty() || (memberId != null && member.get().getMemberId().equals(memberId));
    }

    public List<Member> findAllInstructors() {
        return memberRepository.findByRole(Role.INSTRUCTOR);
    }

    public List<Member> findByRole(Role role) {
        return memberRepository.findByRole(role);
    }

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public void create(String userId, String email, String password, Role role, String name) {
        // 1. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 2. Member 엔티티 생성 및 값 세팅
        Member member = new Member();
        member.setUserId(userId);
        member.setEmail(email);
        member.setPassword(encodedPassword);
        member.setRole(role);
        member.setName(name);

        // 3. DB에 저장
        memberRepository.save(member);
    }

    public void saveFromDto(MemberFormDto dto) {
        Member member = (dto.getMemberId() != null)
                ? memberRepository.findById(dto.getMemberId()).orElse(new Member())
                : new Member();
        member.setUserId(dto.getUserId());
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        member.setRole(Role.valueOf(dto.getRole()));
        // 비밀번호 암호화(수정시 입력 시만)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else if (dto.getMemberId() != null) {
            Member origin = memberRepository.findById(dto.getMemberId()).orElseThrow();
            member.setPassword(origin.getPassword());
        }
        memberRepository.save(member);
    }


    public boolean isAdmin(String loginUserId) {
        return memberRepository.findByUserId(loginUserId)
                .map(m -> m.getRole() == Role.ADMIN)
                .orElse(false);
    }


    public void deleteByUserId(String userId) {
        memberRepository.deleteByUserId(userId);
    }



}
