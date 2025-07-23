package com.edu.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.edu.dto.ReviewDto;
import com.edu.dto.ReviewFormDto;
import com.edu.entity.Lecture;
import com.edu.entity.Member;
import com.edu.entity.Review;
import com.edu.lecture.LectureRepository;
import com.edu.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    public Page<Review> findAll(Pageable pageable) { return reviewRepository.findAll(pageable); }
    public Optional<Review> findById(Long id) { return reviewRepository.findById(id); }
    public List<Review> findByLecture(Long lectureId) { return reviewRepository.findByLecture_LectureId(lectureId); }
    public Page<Review> findByLecturePaged(Long lectureId, Pageable pageable) { return reviewRepository.findByLecture_LectureId(lectureId, pageable); }
    public ReviewDto toDto(Review entity) {
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(entity.getReviewId());
        dto.setLectureId(entity.getLecture().getLectureId());
        dto.setLectureTitle(entity.getLecture().getTitle());
        dto.setUserId(entity.getMember().getUserId());
        dto.setMemberName(entity.getMember().getName());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setRegDate(entity.getRegDate() != null
                ? entity.getRegDate()
                : (entity.getCreatedAt() != null ? entity.getCreatedAt() : null)
        );
        return dto;
    }
    public ReviewFormDto getFormDto(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(this::toFormDto)
                .orElse(new ReviewFormDto());
    }
    private ReviewFormDto toFormDto(Review entity) {
        ReviewFormDto dto = new ReviewFormDto();
        dto.setReviewId(entity.getReviewId());
        dto.setLectureId(entity.getLecture().getLectureId());
        dto.setUserId(entity.getMember().getUserId());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        return dto;
    }
    public void saveFromDto(ReviewFormDto reviewFormDto) {
        Review review = new Review();
        review.setReviewId(reviewFormDto.getReviewId());
        review.setRating(reviewFormDto.getRating());
        review.setComment(reviewFormDto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Lecture lecture = lectureRepository.findById(reviewFormDto.getLectureId())
                .orElseThrow(() -> new IllegalArgumentException("강의 없음"));
        review.setLecture(lecture);

        Member member = memberRepository.findByUserId(reviewFormDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("작성자 없음"));
        review.setMember(member);

        reviewRepository.save(review);
    }
	public List<ReviewDto> findAllDto() {
		List<Review> reviewList = reviewRepository.findAll();
		return reviewList.stream()
				.map(this::toDto)
		        .toList(); // Java 16+ (8 이상이면 .collect(Collectors.toList()))
	}

    public void save(Review review) {
        reviewRepository.save(review);
    }

    public void delete(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
