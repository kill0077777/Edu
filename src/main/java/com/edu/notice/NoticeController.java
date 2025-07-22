package com.edu.notice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.edu.dto.NoticeDto;
import com.edu.entity.Notice;

@Controller
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

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
        model.addAttribute("notice", NoticeDto.fromEntity(notice));
        return "notice/detail";
    }

    // [관리자/강사만] 공지 등록/수정 폼
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @GetMapping({"/form", "/edit/{id}"})
    public String form(@RequestParam(value = "id", required = false) Long id, Model model) {
        Notice notice = (id != null) ? noticeService.getNotice(id) : new Notice();
        model.addAttribute("notice", notice);
        return "notice/form";
    }

    // [관리자/강사만] 공지 저장
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @PostMapping("/save")
    public String save(@ModelAttribute Notice notice) {
        noticeService.saveNotice(notice);
        return "redirect:/notice/list";
    }

    // [관리자/강사만] 공지 삭제
    @PreAuthorize("hasAnyRole('ADMIN','INSTRUCTOR')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/notice/list";
    }
}
