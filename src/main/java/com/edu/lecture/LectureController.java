package com.edu.lecture;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.dto.LectureFormDto;
import com.edu.dto.QuestionDto;
import com.edu.dto.ReviewDto;
import com.edu.enrollment.EnrollmentService;
import com.edu.entity.Enrollment;
import com.edu.entity.Lecture;
import com.edu.entity.LectureStatus;
import com.edu.entity.Member;
import com.edu.entity.Question;
import com.edu.entity.Review;
import com.edu.member.MemberService;
import com.edu.member.MemberUserDetails;
import com.edu.question.QuestionService;
import com.edu.review.ReviewService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class LectureController {
	private final LectureService lectureService;
	private final MemberService memberService;
	private final ReviewService	reviewService;
	private final QuestionService questionService;
	private final EnrollmentService enrollmentService;

	//private final LectureFormDto lectureFormDto;

	// 파일 저장 메서드 (Controller 내부에 둘 수도 있고, 별도 Service로 분리도 가능)
	private static final String UPLOAD_DIR = "/uploads/lecture/";
	private static final String FILE_SAVE_PATH = System.getProperty("user.dir") + UPLOAD_DIR;
    private static final String folderName = null;

	//http://localhost:80/lecture/list
	//http://localhost:8090/lecture/list
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal MemberUserDetails userDetails,  // ⭐️ 추가!
            Model model) {
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lectureId") );
        Page<Lecture> lecturePage = lectureService.findAll(pageable);

        Map<Long, String> lectureStatusMap = new HashMap<>();
        String loginUserId = (userDetails != null) ? userDetails.getUserId() : null;

        if (loginUserId != null) {
        	// ⭐️ 여기에서 각 강의별 진행률 맵을 만든다!
            List<Enrollment> enrollments = enrollmentService.myEnrollments(loginUserId);
            Map<Long, Integer> progressMap = enrollments.stream()
                .collect(Collectors.toMap(e -> e.getLecture().getLectureId(), Enrollment::getProgress));
            for (Lecture lec : lecturePage.getContent()) {
                Integer prog = progressMap.get(lec.getLectureId());
                if (prog == null) {
                    lectureStatusMap.put(lec.getLectureId(), "OPEN");
                } else if (prog == 0) {
                    lectureStatusMap.put(lec.getLectureId(), "OPEN");
                } else if (prog == 100) {
                    lectureStatusMap.put(lec.getLectureId(), "CLOSED");
                } else {
                    lectureStatusMap.put(lec.getLectureId(), "READY");
                }
            }
        } else {
            // 비로그인: 모두 OPEN
            for (Lecture lec : lecturePage.getContent()) {
                lectureStatusMap.put(lec.getLectureId(), "OPEN");
            }
        }

        model.addAttribute("lecturePage", lecturePage);
        model.addAttribute("lectureStatusMap", lectureStatusMap);
        return "lecture/list";
    }


	// [모든 사용자 접근] 강의 상세
	//http://localhost/lecture/detail/1
	// 강의상세 Controller
	//@GetMapping({"/detail/{id}", "/{lectureId}/review"})
	@GetMapping("/detail/{lectureId}")
	public String detail(@PathVariable("lectureId") Long lectureId,
	                     @RequestParam(value = "page", defaultValue = "1") int page,
	                     @RequestParam(value = "size", defaultValue = "10") int size,
	                     @AuthenticationPrincipal MemberUserDetails userDetails, // ⭐️ 추가!
	                     Model model) {
	    Lecture lecture = lectureService.findById(lectureId).orElseThrow();
	    // QnA 페이징
	    Page<Question> questionPage = questionService.findByLecturePaged(lectureId, PageRequest.of(page - 1, size));
	    List<QuestionDto> questionDtoList = questionPage.stream().map(QuestionDto::fromEntity).toList();
	    Page<QuestionDto> questionDtoPage = new PageImpl<>(questionDtoList, questionPage.getPageable(), questionPage.getTotalElements());

	 // 상태 계산
	    String lectureStatus = "OPEN";
	    if (userDetails != null) {
	        Optional<Enrollment> enrollmentOpt = enrollmentService.getEnrollment(lectureId, userDetails.getUserId());
	        if (enrollmentOpt.isPresent()) {
	            int prog = enrollmentOpt.get().getProgress();
	            if (prog == 0) {
					lectureStatus = "OPEN";
				} else if (prog == 100) {
					lectureStatus = "CLOSED";
				} else {
					lectureStatus = "READY";
				}
	        }
	    }

	    // 리뷰 페이징
	    Page<Review> reviewPage = reviewService.findByLecturePaged(lectureId, PageRequest.of(page - 1, size));
	    List<ReviewDto> reviewDtoList = reviewPage.stream().map(ReviewDto::fromEntity).toList();
	    Page<ReviewDto> reviewDtoPage = new PageImpl<>(reviewDtoList, reviewPage.getPageable(), reviewPage.getTotalElements());

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String loginUserId = (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) ? auth.getName() : null;

	    // 권한
	    boolean canEdit = false;
	    if (loginUserId != null) {
	        boolean isInstructor = lecture.getInstructor() != null && loginUserId.equals(lecture.getInstructor().getUserId());
	        boolean isAdmin = memberService.isAdmin(loginUserId);
	        canEdit = isInstructor || isAdmin;
	    }

	    // 수강생이면서 리뷰 가능여부 (여기선 로직 샘플)
	    boolean canWriteReview = (loginUserId != null) && lectureService.isLectureStudent(lectureId, loginUserId);

	    model.addAttribute("lecture", lecture);
	    model.addAttribute("lectureStatus", lectureStatus);
	    model.addAttribute("questionPage", questionDtoPage);
	    model.addAttribute("reviewPage", reviewDtoPage);
	    model.addAttribute("canEdit", canEdit);
	    model.addAttribute("canWriteReview", canWriteReview);

	    return "lecture/detail";
	}


	// [관리자/강사만] 강의 등록/수정 폼
	// 수정 폼: ADMIN 또는 본인 INSTRUCTOR만 접근
	//http://localhost/lecture/form
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	// 1) 신규 등록: /lecture/form  2) 수정: /lecture/edit/{lectureId} (PathVariable)
	@GetMapping({"/form", "/edit/{lectureId}"})
	public String form(
	        @PathVariable(value = "lectureId", required = false) Long lectureId,
	        @RequestParam(value = "lectureId", required = false) Long reqLectureId, // 혹시 쿼리파라미터로도 지원
	        Model model) {

	    // URL 타입별로 lectureId 매핑
	    Long id = (lectureId != null) ? lectureId : reqLectureId;

	    LectureFormDto lectureFormDto;
	    if (id != null) { // 수정폼
	        Lecture lecture = lectureService.findById(id)
	                .orElseThrow(() -> new AccessDeniedException("존재하지 않는 강의입니다."));
	        checkEditPermission(lecture); // 권한 체크
	        lectureFormDto = lectureService.getFormDto(id);
	    } else { // 신규등록 폼
	        lectureFormDto = new LectureFormDto();
	    }

	    // 강사 목록(없을 때 빈 리스트)
	    List<Member> instructorList = memberService.findAllInstructors();
	    if (instructorList == null) {
			instructorList = new ArrayList<>();
		}

	    // 필수 model 데이터 세팅
	    model.addAttribute("statusList", LectureStatus.values());
	    model.addAttribute("lectureFormDto", lectureFormDto);
	    model.addAttribute("instructorList", instructorList);

	    return "lecture/form";
	}

	// [관리자/강사만] 강의 저장
	// 저장 (등록/수정): ADMIN 또는 본인 INSTRUCTOR만 가능 (권장: 추가로 checkEditPermission 호출)
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	@PostMapping("/save")
	public String save(@ModelAttribute LectureFormDto lectureFormDto, Model model) throws IOException {
		List<String> thumbnailPaths = new ArrayList<>();
		if (lectureFormDto.getThumbnailFiles() != null && !lectureFormDto.getThumbnailFiles().isEmpty()) {
			for (MultipartFile file : lectureFormDto.getThumbnailFiles()) {
				if (!file.isEmpty()) {
					thumbnailPaths.add(saveThumbnail(file, folderName));
				}
			}
		}
		lectureFormDto.setThumbnailPaths(thumbnailPaths);
		Member instructor = memberService.findByUserId(lectureFormDto.getUserId()).orElse(null);
		Integer price = (lectureFormDto.getPrice() == null) ? 0 : lectureFormDto.getPrice();

		LectureStatus statusEnum;
		try {
			statusEnum = (lectureFormDto.getStatus() != null)
				? LectureStatus.valueOf(lectureFormDto.getStatus())
						: LectureStatus.OPEN;
		} catch (Exception e) {
			statusEnum = LectureStatus.OPEN;
		}

		Lecture lecture = Lecture.builder()
				.lectureId(lectureFormDto.getLectureId())
				.title(lectureFormDto.getTitle())
				.description(lectureFormDto.getDescription())
				.category(lectureFormDto.getCategory())
				.price(price)
				.status(statusEnum)
				.instructor(instructor)
				.thumbnail(thumbnailPaths.isEmpty() ? null : thumbnailPaths.get(0))
				.thumbnails(thumbnailPaths)
				.build();
		lectureService.save(lecture);
		return "redirect:/lecture/list";
	}



	// [관리자/강사만] 공지 삭제
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
		Lecture lecture = lectureService.findById(id).orElseThrow(() -> new AccessDeniedException("존재하지 않는 강의입니다."));
		checkEditPermission(lecture);
		boolean deleted = lectureService.delete(id);
		if (deleted) {
			redirectAttributes.addFlashAttribute("msg", "삭제되었습니다.");
		} else {
			redirectAttributes.addFlashAttribute("msg", "존재하지 않는 강의입니다.");
		}
		return "redirect:/lecture/list";
	}

	// --- [권한 체크 로직] ---
	private void checkEditPermission(Lecture lecture) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			throw new AccessDeniedException("권한 없음");
		}
		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			return;
		}
		if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"))) {
			String loginUserId = auth.getName();
			String instructorUserId = (lecture.getInstructor() != null) ? lecture.getInstructor().getUserId() : null;
			if (loginUserId != null && loginUserId.equals(instructorUserId)) {
				return;
			}
		}
		throw new AccessDeniedException("권한 없음");
	}


	// 파일 저장 (확장자 없는 파일도 처리)
	private String saveThumbnail(MultipartFile file, String folderName) throws IOException {
		String originalFilename = file.getOriginalFilename();
		String ext = "";
		int idx = originalFilename.lastIndexOf('.');
		if (idx >= 0) {
			ext = originalFilename.substring(idx);
		}
		String savedFilename = UUID.randomUUID().toString() + ext;
		File uploadDir = new File(FILE_SAVE_PATH + folderName);
		if (!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		File saveFile = new File(uploadDir, savedFilename);
		file.transferTo(saveFile);
		return UPLOAD_DIR + folderName + "/" + savedFilename;
	}
}