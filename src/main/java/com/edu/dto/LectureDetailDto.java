package com.edu.dto;

import java.util.List;

import lombok.Data;

@Data
public class LectureDetailDto { //강의 상세조회(상세페이지) 응답 DTO
	private Long lectureId;
	private String userId;       // 회원 로그인 아이디 (email 등)
	private String title;
	private String description;
	private String instructorName;
	private String category;
	private Integer price;
	private String status;
	private String lectureStatus;
	private String thumbnail; // 이미지 URL 또는 경로
	private String regDate;
    // 상세 페이지에서만 필요
	private List<LectureContentDto> contentList;
	private List<ReviewDto> reviewList;
	private List<QuestionDto> questionList;
}
