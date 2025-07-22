package com.edu.review;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.edu.dto.ReviewDto;
import com.edu.dto.ReviewFormDto;
import com.edu.entity.Lecture;
import com.edu.entity.Review;
import com.edu.lecture.LectureService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final LectureService lectureService;

    // 전체 리뷰 목록 (lectureId 없이)
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
    	Page<Review> reviewPage = reviewService.findAll(PageRequest.of(page - 1, size));
    	List<ReviewDto> reviewDtoList = reviewPage.stream()
    	        .map(ReviewDto::fromEntity)
    	        .collect(Collectors.toList());
    	model.addAttribute("reviewPage", reviewPage);
    	model.addAttribute("reviewList", reviewDtoList);  // reviewList만 화면에서 반복 돌리기
        return "review/list";
    }

    // 강의별 리뷰 목록
    @GetMapping("/lecture/{lectureId}/review")
    public String lectureReviewList(
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<Review> reviewPage = reviewService.findByLecturePaged(lectureId, PageRequest.of(page - 1, size));
        List<ReviewDto> reviewDtoList = reviewPage.stream().map(ReviewDto::fromEntity).collect(Collectors.toList());
        Page<ReviewDto> reviewDtoPage = new PageImpl<>(reviewDtoList, reviewPage.getPageable(), reviewPage.getTotalElements());
        model.addAttribute("reviewPage", reviewDtoPage); // ★이것만 쓰세요!
        model.addAttribute("lectureId", lectureId);
        return "review/list";
    }

    // 상세보기
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Optional<Review> optionalReview = reviewService.findById(id);
        ReviewDto reviewDto = optionalReview.map(ReviewDto::fromEntity).orElse(new ReviewDto());
        model.addAttribute("review", reviewDto);  // Dto만 넘겨!
        return "review/detail";
    }

    // 작성/수정 폼
    @GetMapping({"/form", "/edit/{id}"})
    public String form(
            @PathVariable(value = "id", required = false) Long reviewId,
            @RequestParam(value = "lectureId", required = false) Long lectureId,
            @RequestParam(value = "userId", required = false) String userId,
            Model model) {
        ReviewFormDto reviewFormDto = (reviewId != null) ? reviewService.getFormDto(reviewId) : new ReviewFormDto();
        if (lectureId != null) {
			reviewFormDto.setLectureId(lectureId);
		}
        if (userId != null) {
			reviewFormDto.setUserId(userId);
		}

        List<Lecture> lectureList = lectureService.getLecturesForReview();
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("reviewId", reviewId);
        model.addAttribute("reviewFormDto", reviewFormDto);
        model.addAttribute("lectureList", lectureList);
        return "review/form";
    }

    @PostMapping("/save")
    public String saveReview(@ModelAttribute ReviewFormDto reviewFormDto, Model model) {
        Lecture lecture = lectureService.findById(reviewFormDto.getLectureId()).orElse(null);
        if (lecture == null) {
            model.addAttribute("error", "해당 강의를 찾을 수 없습니다.");
            model.addAttribute("reviewFormDto", reviewFormDto);
            List<Lecture> lectureList = lectureService.getLecturesForReview();
            model.addAttribute("lectureList", lectureList);
            return "review/form";
        }
        if (!List.of("READY", "CLOSED", "END").contains(lecture.getStatus().name())) {
            model.addAttribute("error", "이 강의에는 리뷰를 작성할 수 없습니다.");
            model.addAttribute("reviewFormDto", reviewFormDto);
            List<Lecture> lectureList = lectureService.getLecturesForReview();
            model.addAttribute("lectureList", lectureList);
            return "review/form";
        }
        reviewService.saveFromDto(reviewFormDto);
        return "redirect:/review/lecture/" + reviewFormDto.getLectureId() + "/review";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @RequestParam(value = "lectureId", required = false) Long lectureId) {
        reviewService.delete(id);
        // 삭제 후 lectureId가 있으면 강의별 리뷰로, 없으면 전체 리뷰로 이동
        if (lectureId != null) {
            return "redirect:/review/lecture/" + lectureId + "/review";
        }
        return "redirect:/review/list";
    }
}
