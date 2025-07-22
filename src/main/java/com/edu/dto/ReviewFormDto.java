package com.edu.dto;

import lombok.Data;

@Data
public class ReviewFormDto {
	private Long reviewId;     // 리뷰 식별자 (수정시 필요)
	private Long lectureId;    // 어떤 강의에 대한 리뷰인지
	private Long memberId;
	private String userId;		// 작성자(로그인 사용자) id
    private Integer rating;    // 평점 (1~5)
    private String comment;    // 리뷰 내용

    private String title;
    private String content;
    // 필요하다면 아래처럼 파일 첨부 등도 추가 가능
    //private MultipartFile attachment;


}
