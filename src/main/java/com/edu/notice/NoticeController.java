package com.edu.notice;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edu.dto.NoticeDto;
import com.edu.dto.NoticeFormDto;
import com.edu.entity.Member;
import com.edu.entity.Notice;
import com.edu.member.MemberService;

@Controller
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private MemberService memberService;

    // [모든 사용자 접근] 공지사항 목록
    @GetMapping("/list")
    public String list(@RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "size", defaultValue = "10") int size,
                       Model model) {
        Pageable pageable = PageRequest.of(page, size);
        List<NoticeDto> fixedList = noticeService.getFixedNotices();
        Page<NoticeDto> noticeList = noticeService.getGeneralNotices(pageable);
        model.addAttribute("fixedList", fixedList);
        model.addAttribute("noticeList", noticeList);
        return "notice/list";
    }

    // [모든 사용자 접근] 공지 상세
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        noticeService.increaseHit(id);
        Notice notice = noticeService.getNotice(id);
        if (notice == null) {
            model.addAttribute("msg", "해당 공지사항이 존재하지 않습니다.");
            return "notice/detail";
        }
        model.addAttribute("notice", NoticeDto.fromEntity(notice));
        return "notice/detail";
    }

	// [관리자/강사만] 공지 등록/수정 폼
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @GetMapping({"/form", "/edit/{id}"})
    public String form(@RequestParam(value = "id", required = false) Long id,
                       Model model,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {
        NoticeFormDto noticeFormDto;
        if (id != null) {
            Notice notice = noticeService.getNotice(id);
            if (notice == null) {
                redirectAttributes.addFlashAttribute("msg", "존재하지 않는 공지입니다.");
                return "redirect:/notice/list";
            }
            String currentUserId = principal.getName();
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isInstructor = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));

            // 관리자 OK / 강사는 본인글만 OK
            if (isAdmin || (isInstructor && notice.getWriter() != null && notice.getWriter().getUserId().equals(currentUserId))) {
                noticeFormDto = NoticeFormDto.fromEntity(notice);
            } else {
                redirectAttributes.addFlashAttribute("msg", "본인 작성글만 수정할 수 있습니다.");
                return "redirect:/notice/list";
            }
        } else {
            noticeFormDto = new NoticeFormDto();
        }
        model.addAttribute("noticeFormDto", noticeFormDto);
        return "notice/form";
    }


    // [관리자/강사만] 저장 (등록/수정)
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @PostMapping("/save")
    public String save(@ModelAttribute NoticeFormDto noticeFormDto,
                       Principal principal,
                       Model model) {
        Notice notice;
        String currentUserId = principal.getName();
        Member writer = memberService.findByUserId(currentUserId).orElse(null);

        if (writer == null) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }

        if (noticeFormDto.getNoticeId() != null) {
            // 수정
            notice = noticeService.getNotice(noticeFormDto.getNoticeId());
            if (notice == null) {
				return "redirect:/notice/list";
			}
            // 본인 글만 수정 가능, 작성자 없으면 자동 등록
            if (notice.getWriter() != null) {
                if (!notice.getWriter().getUserId().equals(currentUserId)) {
                    model.addAttribute("msg", "본인 작성글만 수정할 수 있습니다.");
                    return "redirect:/notice/list";
                }
            } else {
                notice.setWriter(writer);
            }
            notice.setTitle(noticeFormDto.getTitle());
            notice.setContent(noticeFormDto.getContent());
            notice.setFixedFlag(Boolean.TRUE.equals(noticeFormDto.getFixedFlag()));
        } else {
            // 신규 등록
            notice = Notice.builder()
                    .title(noticeFormDto.getTitle())
                    .content(noticeFormDto.getContent())
                    .fixedFlag(Boolean.TRUE.equals(noticeFormDto.getFixedFlag()))
                    .hit(0)
                    .writer(writer)
                    .build();
        }
        noticeService.saveNotice(notice);
        return "redirect:/notice/list";
    }

    // [관리자/강사만] 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/notice/list";
        }
        String currentUserId = principal.getName();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isInstructor = authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_INSTRUCTOR"));

        String msg = noticeService.deleteNotice(id, currentUserId, isAdmin, isInstructor);
        redirectAttributes.addFlashAttribute("msg", msg);

        return "redirect:/notice/list";
    }

}
