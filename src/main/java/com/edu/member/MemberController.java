package com.edu.member;



import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.edu.dto.LoginForm;
import com.edu.dto.MemberFormDto;
import com.edu.entity.Member;
import com.edu.entity.Role;
import com.edu.lecture.LectureService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	private final LectureService lectureService;

	//http://localhost:80/member/list
	// 회원 목록 (ADMIN만)
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/list")
	public String list(	@RequestParam(value = "page", defaultValue = "1") int page,
						@RequestParam(value = "size", defaultValue = "10") int size,
						Model model) {
		Page<Member> memberPage = memberService.findAll(PageRequest.of(page - 1, size));
		model.addAttribute("memberPage", memberPage);
		return "member/list";
    }

	//http://localhost:80/member/profile
	@GetMapping("/profile")
	public String profile(Model model, @AuthenticationPrincipal MemberUserDetails userDetails) {
		model.addAttribute("member", userDetails.getMember());
		return "profile"; // /templates/profile.html or profile.jsp 렌더링
	}

	//http://localhost:80/member/detail/2
	// 회원 상세 (userId) (ADMIN만)
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/detail/{userId}")
	public String detail(@PathVariable(value =  "userId") String userId, Model model) {
		Member member = memberService.findByUserId(userId).orElse(null);
		model.addAttribute("member", member);
		return "member/detail";
	}

	// 로그인
	// GET http://localhost:80/member/login
	@GetMapping("/login")
	public String login() {
		return "login_form";
	}

	//POST http://localhost:80/member/login
	@PostMapping("/login")
	public String loginSubmit(@ModelAttribute LoginForm form) {
	    // 로그인 처리
		// 콘솔 로그로 파라미터 확인
	   // System.out.println("userId = " + userId);
	    //System.out.println("password = " + password); // 테스트 후 반드시 지우세요! 보안주의
	    return "redirect:/";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute MemberFormDto memberFormDto) {
	    memberService.registerMember(memberFormDto);
	    return "redirect:/login";
	}

	//GET http://localhost:80/member/signup
	//회원가입 폼 으로 이동 요청
	@GetMapping("/signup")
	public String signupForm(MemberFormDto memberFormDto) {
		return "signup_form";
	}

	//POST http://localhost:80/member/signup
	//회원가입 요청
	@PostMapping("/signup")
	public String signup(@Valid MemberFormDto memberFormDto, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "signup_form";
		}
		try {
			memberService.create(
					memberFormDto.getUserId(),   // String userId로 받음
					memberFormDto.getEmail(),
					memberFormDto.getPassword(),
					Role.valueOf(memberFormDto.getRole()),
					memberFormDto.getName()
			);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
			return "signup_form";
		} catch (Exception e) {
			e.printStackTrace();
			bindingResult.reject("signupFailed", e.getMessage());
			return "signup_form";
		}
		return "redirect:/user/login";
    }


	 // 회원 등록/수정 폼 (ADMIN만)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping({"/form", "/edit/{userId}"})
    public String form(@PathVariable(value = "userId", required = false) String userId, Model model) {
        MemberFormDto dto = (userId != null)
                ? memberService.getFormDtoByUserId(userId)
                : new MemberFormDto();
        model.addAttribute("memberFormDto", dto);
        return "member/form";
    }


 // 회원 저장(등록/수정) (ADMIN만)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("memberFormDto") MemberFormDto dto, BindingResult bindingResult, Model model) {
        // 중복체크
        if (!memberService.isUniqueUserId(dto.getUserId(), dto.getMemberId())) {
            bindingResult.rejectValue("userId", "duplicate", "이미 사용중인 아이디입니다.");
        }
        if (!memberService.isUniqueEmail(dto.getEmail(), dto.getMemberId())) {
            bindingResult.rejectValue("email", "duplicate", "이미 사용중인 이메일입니다.");
        }
        if (bindingResult.hasErrors()) {
            return "member/form";
        }
        try {
            memberService.saveFromDto(dto);
        } catch (DataIntegrityViolationException e) {
            bindingResult.reject("error", "DB 오류가 발생했습니다.");
            return "member/form";
        }
        return "redirect:/member/list";
    }

 // 회원 삭제 (ADMIN만, userId로)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{userId}")
    public String delete(@PathVariable String userId) {
        memberService.deleteByUserId(userId);
        return "redirect:/member/list";
    }
}
