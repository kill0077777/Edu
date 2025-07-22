package com.edu.enrollment;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.member.MemberUserDetails;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/enrollment")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    // 수강 신청
    @PostMapping("/enroll")
    public String enroll(@RequestParam Long lectureId,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        String loginUserId = userDetails.getUserId(); // getUsername() or getUserId()로 통일!
        try {
            enrollmentService.enroll(lectureId, loginUserId);
            redirectAttributes.addFlashAttribute("msg", "수강신청 완료!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("msg", "수강신청 실패: " + e.getMessage());
        }
        return "redirect:/lecture/detail/" + lectureId;
    }

    // 내 수강 이력(마이페이지)
    @GetMapping("/my")
    public String myEnrollments(@AuthenticationPrincipal MemberUserDetails userDetails, Model model) {
        String loginUserId = userDetails.getUserId(); // getUsername() or getUserId()로 통일!
        model.addAttribute("enrollmentList", enrollmentService.getMyEnrollments(loginUserId));
        return "enrollment/my";
    }
}
