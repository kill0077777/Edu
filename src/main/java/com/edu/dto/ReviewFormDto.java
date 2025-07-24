package com.edu.dto;

import com.edu.entity.Review;
import lombok.Data;

@Data
public class ReviewFormDto {
	private Long reviewId;     // 리뷰 식별자 (수정시 필요)
	private Long lectureId;    // 어떤 강의에 대한 리뷰인지
	private Long memberId;
	private String userId;     // 작성자(로그인 사용자) id
	private Integer rating;    // 평점 (1~5)
	private String comment;    // 리뷰 내용
	private String title;
	private String content;

	// fromEntity 추가
	public static ReviewFormDto fromEntity(Review review) {
		ReviewFormDto dto = new ReviewFormDto();
		dto.setReviewId(review.getReviewId());
		dto.setLectureId(review.getLecture() != null ? review.getLecture().getLectureId() : null);
		dto.setMemberId(review.getMember() != null ? review.getMember().getMemberId() : null);
		dto.setUserId(review.getMember() != null ? review.getMember().getUserId() : null);
		dto.setRating(review.getRating());
		dto.setComment(review.getComment());
		dto.setTitle(review.getTitle());    // 만약 엔티티에 title 필드가 있으면
		dto.setContent(review.getContent()); // 만약 엔티티에 content 필드가 있으면
		return dto;
    }
}
