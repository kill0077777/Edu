package com.edu.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Lecture;
import com.edu.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	@Override
	Page<Question> findAll(Pageable pageable);
	Page<Question> findByLecture_LectureId(Long lectureId, Pageable pageable);
    List<Question> findByLecture_LectureId(Long lectureId);
    List<Question> findByMember_MemberId(Long memberId);      // PK(Long)로 찾기
    List<Question> findByLecture(Lecture lecture);
    List<Question> findByMember_UserId(String userId);          // userId(Long)로 찾기
}