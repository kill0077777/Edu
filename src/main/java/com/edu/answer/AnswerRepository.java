package com.edu.answer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

	// 기존 PK(memberId) → userId(문자열)로 변경
	List<Answer> findByMember_UserId(String userId); //  ✅ OK   ⭐️ userId(문자열)로 조회
	List<Answer> findByMember_MemberId(Long memberId); //  ✅ OK
	List<Answer> findByQuestion_QuestionId(Long questionId); //  ✅ OK

	//Optional<Member> findByUserId(String userId);
    //List<Member> findByRole(String role); // ← 이건 OK!

	// 특정 강의의 모든 답변 조회:
	List<Answer> findByQuestion_Lecture_LectureId(Long lectureId);
	//특정 회원이 특정 질문에 단 답변 조회
	List<Answer> findByMember_MemberIdAndQuestion_QuestionId(Long memberId, Long questionId);
	// 특정 회원(userId)이 특정 질문에 단 답변
	List<Answer> findByMember_UserIdAndQuestion_QuestionId(String userId, Long questionId);
}