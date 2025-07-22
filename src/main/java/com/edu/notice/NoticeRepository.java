package com.edu.notice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	// 공지(상단)만 가져오기
	List<Notice> findByFixedFlagTrueOrderByNoticeIdDesc();

	// 일반글만 페이징
	Page<Notice> findByFixedFlagFalseOrderByNoticeIdDesc(Pageable pageable);

    // Notice 전체를 조회
    Page<Notice> findAllByOrderByFixedFlagDescRegDateDesc(Pageable Pageable);

    // 작성자(userId)로 공지 목록 조회 (userId는 String)
    List<Notice> findByWriter_UserId(String userId);

	//Notice save(NoticeFormDto noticeFormDto, LectureStatus status);

    // 고정 공지만 조회
    // List<Notice> findByFixedFlagTrueOrderByRegDateDesc();

    // 일반 공지만 조회
    // List<Notice> findByFixedFlagFalseOrderByRegDateDesc();
}
