package com.edu.member;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Member;
import com.edu.entity.Role;


public interface MemberRepository extends JpaRepository<Member, Long> {


	// PK로 조회 (PK는 여전히 Long memberId)
	Optional<Member> findByMemberId(Long memberId);

	// userId (String) 기준 조회
	//@Query("SELECT m FROM Member m WHERE m.userId = :userId")
	//Optional<Member> findByUserId(@Param("userId") String userId);
	Optional<Member> findByUserId(String userId); // ★ Entity 반환!
	//Optional<MemberFormDto> findByUserId(String userId);
	//@Query("select new com.edu.dto.MemberFormDto(m.userId, m.name, m.email) from Member m where m.userId = :userId")
	//Optional<MemberFormDto> findByUserId(@Param("userId") String userId);

	//email (String) 기준 조회
	Optional<Member> findByEmail(String email);

	void deleteByUserId(String userId);

	// userId (String) 기준 존재 체크
	boolean existsByUserId(String userId);

	// email 존재 체크
	boolean existsByEmail(String email);

 	// 역할별 회원 전체 조회 (INSTRUCTOR, STUDENT 등)
    List<Member> findByRole(Role role);

	List<Member> findByRole(String string);
}
