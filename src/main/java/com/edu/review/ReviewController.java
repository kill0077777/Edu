package com.edu.review;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.dto.ReviewDto;
import com.edu.dto.ReviewFormDto;
import com.edu.entity.Lecture;
import com.edu.entity.Review;
import com.edu.lecture.LectureService;
import com.edu.member.MemberService;
import com.edu.enrollment.EnrollmentService; // 수강신청 체크

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewService reviewService;
    private final LectureService lectureService;
    private final MemberService memberService;
    private final EnrollmentService enrollmentService; // 수강신청 서비스

    // 전체 리뷰 목록
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Page<Review> reviewPage = reviewService.findAll(PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "reviewId")));
        List<ReviewDto> reviewDtoList = reviewPage.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
        Page<ReviewDto> reviewDtoPage = new PageImpl<>(reviewDtoList, reviewPage.getPageable(), reviewPage.getTotalElements());

        model.addAttribute("reviewPage", reviewDtoPage);
        return "review/list";
    }

    // 강의별 리뷰 목록 (강의상세 페이지에서 reviewPage 변수만 사용)
    @GetMapping("/lecture/{lectureId}/review")
    public String lectureReviewList(
            @PathVariable("lectureId") Long lectureId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, Principal principal) {

        Page<Review> reviewPage = reviewService.findByLecturePaged(lectureId, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "reviewId")));
        List<ReviewDto> reviewDtoList = reviewPage.stream().map(ReviewDto::fromEntity).collect(Collectors.toList());
        Page<ReviewDto> reviewDtoPage = new PageImpl<>(reviewDtoList, reviewPage.getPageable(), reviewPage.getTotalElements());

        // 본인이 이 강의를 수강신청했는지 체크
        boolean canWriteReview = false;
        String userId = (principal != null) ? principal.getName() : null;
        if (userId != null) {
            canWriteReview = enrollmentService.isEnrolled(userId, lectureId);
        }

        model.addAttribute("reviewPage", reviewDtoPage);
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("canWriteReview", canWriteReview);
        return "review/list";
    }

    // 리뷰 등록 폼 (강의상세에서 리뷰 작성버튼 클릭시)
    @GetMapping("/form")
    public String form(@RequestParam("lectureId") Long lectureId,
                       Principal principal,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        String userId = (principal != null) ? principal.getName() : null;
        if (userId == null || !enrollmentService.isEnrolled(userId, lectureId)) {
            redirectAttributes.addFlashAttribute("msg", "수강신청한 학생만 리뷰 작성이 가능합니다.");
            return "redirect:/lecture/detail/" + lectureId;
        }
        ReviewFormDto reviewFormDto = new ReviewFormDto();
        reviewFormDto.setLectureId(lectureId);
        reviewFormDto.setUserId(userId);
        model.addAttribute("reviewFormDto", reviewFormDto);
        return "review/form";
    }

    // 리뷰 등록 처리
    @PostMapping("/save")
    public String saveReview(@ModelAttribute ReviewFormDto reviewFormDto,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        String userId = (principal != null) ? principal.getName() : null;
        Long lectureId = reviewFormDto.getLectureId();
        if (userId == null || !enrollmentService.isEnrolled(userId, lectureId)) {
            redirectAttributes.addFlashAttribute("msg", "수강신청한 학생만 리뷰 작성이 가능합니다.");
            return "redirect:/lecture/detail/" + lectureId;
        }
        reviewFormDto.setUserId(userId);
        reviewService.saveFromDto(reviewFormDto);
        redirectAttributes.addFlashAttribute("msg", "리뷰가 등록되었습니다.");
        return "redirect:/lecture/detail/" + lectureId;
    }

    // 리뷰 수정 폼
    @GetMapping("/edit/{reviewId}")
    public String editForm(@PathVariable("reviewId") Long reviewId,
                           Principal principal,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        Review review = reviewService.findById(reviewId).orElse(null);
        if (review == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 리뷰입니다.");
            return "redirect:/lecture/list";
        }
        String currentUserId = (principal != null) ? principal.getName() : null;
        boolean isAdmin = memberService.isAdmin(currentUserId);
        if (!isAdmin && (review.getMember() == null || !review.getMember().getUserId().equals(currentUserId))) {
            redirectAttributes.addFlashAttribute("msg", "본인만 수정할 수 있습니다.");
            return "redirect:/lecture/detail/" + review.getLecture().getLectureId();
        }
        model.addAttribute("reviewFormDto", ReviewFormDto.fromEntity(review));
        return "review/edit";
    }

    // 리뷰 수정 처리
    @PostMapping("/edit/{reviewId}")
    public String edit(@PathVariable Long reviewId,
                       @ModelAttribute ReviewFormDto reviewFormDto,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {
        Review review = reviewService.findById(reviewId).orElse(null);
        if (review == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 리뷰입니다.");
            return "redirect:/lecture/list";
        }
        String currentUserId = (principal != null) ? principal.getName() : null;
        boolean isAdmin = memberService.isAdmin(currentUserId);
        if (!isAdmin && (review.getMember() == null || !review.getMember().getUserId().equals(currentUserId))) {
            redirectAttributes.addFlashAttribute("msg", "본인만 수정할 수 있습니다.");
            return "redirect:/lecture/detail/" + review.getLecture().getLectureId();
        }
        // 내용 업데이트
        review.setContent(reviewFormDto.getContent());
        review.setRating(reviewFormDto.getRating());
        reviewService.save(review);

        redirectAttributes.addFlashAttribute("msg", "리뷰가 수정되었습니다.");
        return "redirect:/lecture/detail/" + review.getLecture().getLectureId();
    }

    // 리뷰 삭제 (POST)
    @PostMapping("/delete/{reviewId}")
    public String delete(@PathVariable Long reviewId, Principal principal, RedirectAttributes redirectAttributes) {
        Review review = reviewService.findById(reviewId).orElse(null);
        if (review == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 리뷰입니다.");
            return "redirect:/lecture/list";
        }
        String currentUserId = (principal != null) ? principal.getName() : null;
        boolean isAdmin = memberService.isAdmin(currentUserId);
        if (!isAdmin && (review.getMember() == null || !review.getMember().getUserId().equals(currentUserId))) {
            redirectAttributes.addFlashAttribute("msg", "본인만 삭제할 수 있습니다.");
            return "redirect:/lecture/detail/" + review.getLecture().getLectureId();
        }
        Long lectureId = review.getLecture().getLectureId();
        reviewService.delete(reviewId);
        redirectAttributes.addFlashAttribute("msg", "리뷰가 삭제되었습니다.");
        return "redirect:/lecture/detail/" + lectureId;
    }

    // 상세보기 (필요시)
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Optional<Review> optionalReview = reviewService.findById(id);
        ReviewDto reviewDto = optionalReview.map(ReviewDto::fromEntity).orElse(new ReviewDto());
        model.addAttribute("review", reviewDto);
        return "review/detail";
    }
}
