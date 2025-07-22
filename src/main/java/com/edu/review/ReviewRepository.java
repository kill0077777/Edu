package com.edu.review;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, Long> {

	@Override
	// 전체 페이징 리뷰
	Page<Review> findAll(Pageable pageable);
	// 강의별 전체 리뷰(페이징X)
	List<Review> findByLecture_LectureId(Long lectureId);
	// 강의별 페이징 리뷰
	Page<Review> findByLecture_LectureId(Long lectureId, Pageable pageable);


	List<Review> findByMember_MemberId(Long memberId); // 멤버 PK (Long)
	List<Review> findByMember_UserId(String userId);     // 회원 userId (Long)

	// 강사 리스트 등 추가 (예시)
	// @Query("SELECT m FROM Member m WHERE m.role = 'INSTRUCTOR'")
	// List<Member> findAllInstructors();

}