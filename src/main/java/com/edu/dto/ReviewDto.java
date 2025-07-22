package com.edu.dto;

import java.time.LocalDateTime;

import com.edu.entity.Review;

import lombok.Data;

//com.edu.dto.ReviewDto.java
@Data
public class ReviewDto {
	private Long reviewId;
	private Long lectureId;
	private String lectureTitle;
	private Long memberId;
	private String memberName;
	private String userId;
	private Integer rating;
	private String comment;
	private LocalDateTime regDate;


	//엔티티 → DTO 변환
	public static ReviewDto fromEntity(Review review) {
	    ReviewDto reviewDto = new ReviewDto();
	    reviewDto.setReviewId(review.getReviewId());
	    reviewDto.setLectureId(review.getLecture() != null ? review.getLecture().getLectureId() : null);
	    reviewDto.setLectureTitle(review.getLecture() != null ? review.getLecture().getTitle() : null);
	    reviewDto.setMemberId(review.getMember() != null ? review.getMember().getMemberId() : null);
	    reviewDto.setUserId(review.getMember() != null ? review.getMember().getUserId() : null);
	    reviewDto.setMemberName(review.getMember() != null ? review.getMember().getName() : null);
	    reviewDto.setRating(review.getRating());
	    reviewDto.setComment(review.getComment());
	    // 날짜 우선순위
	    if (review.getCreatedAt() != null) {
			reviewDto.setRegDate(review.getCreatedAt());
		} else if (review.getRegDate() != null) {
			reviewDto.setRegDate(review.getRegDate());
		}
	    return reviewDto;
	}



	// 엔티티 → DTO 변환 (정적 팩토리 메서드)
	public static ReviewDto fromEntity1(Review review) {
		ReviewDto reviewDto = new ReviewDto();
		if (review == null) {
			return reviewDto; // null 체크
		}

		reviewDto.setReviewId(review.getReviewId());
		if (review.getLecture() != null) {
			reviewDto.setLectureId(review.getLecture().getLectureId());
			reviewDto.setLectureTitle(review.getLecture().getTitle());
		}
		if (review.getMember() != null) {
			reviewDto.setMemberId(review.getMember().getMemberId());
			reviewDto.setUserId(review.getMember().getUserId());
			reviewDto.setMemberName(review.getMember().getName());
		}
		reviewDto.setRating(review.getRating());
		reviewDto.setComment(review.getComment());

        // 날짜: createdAt 우선, 둘 다 null이면 null
		LocalDateTime regDate = null;
		if (review.getCreatedAt() != null) {
			regDate = review.getCreatedAt();
		} else if (review.getRegDate() != null) {
			regDate = review.getRegDate();
		}
		reviewDto.setRegDate(regDate);

		return reviewDto;
	}
}