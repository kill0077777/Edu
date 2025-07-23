package com.edu.notice;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.edu.dto.NoticeDto;
import com.edu.entity.Notice;

import jakarta.transaction.Transactional;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    // 페이징 + 고정/최신순
    public Page<Notice> getAllNotices(PageRequest pageRequest) {
        return noticeRepository.findAllByOrderByFixedFlagDescRegDateDesc(pageRequest);
    }

    // 고정 공지 (상단)
    public List<NoticeDto> getFixedNotices() {
        return noticeRepository.findByFixedFlagTrueOrderByNoticeIdDesc()
            .stream().map(NoticeDto::fromEntity).collect(Collectors.toList());
    }

    // 일반(비고정) 공지 (페이징)
    public Page<NoticeDto> getGeneralNotices(Pageable pageable) {
        return noticeRepository.findByFixedFlagFalseOrderByNoticeIdDesc(pageable)
            .map(NoticeDto::fromEntity);
    }

    // 특정 유저의 공지 목록
    public List<Notice> getNoticesByUserId(String userId) {
        return noticeRepository.findByWriter_UserId(userId);
    }

    // 단일 조회
    public Notice getNotice(Long id) {
        return noticeRepository.findById(id).orElse(null);
    }

    // 단일 조회 (Optional)
    public Optional<Notice> findById(Long id) {
        return noticeRepository.findById(id);
    }

    // 저장(등록/수정)
    public Notice saveNotice(Notice notice) {
        if (notice.getHit() == null) {
			notice.setHit(0); // 신규 등록 NPE 방지!
		}
        return noticeRepository.save(notice);
    }

	// 삭제(권한체크 + 메시지 반환)
    public String deleteNotice(Long noticeId, String userId, boolean isAdmin, boolean isInstructor) {
        Optional<Notice> opt = noticeRepository.findById(noticeId);
        if (opt.isEmpty()) {
            return "이미 삭제된 공지입니다.";
        }
        Notice notice = opt.get();

        if (isAdmin) {
            noticeRepository.delete(notice);
            return "공지 삭제 완료!";
        }
        if (isInstructor) {
            if (notice.getWriter() != null && notice.getWriter().getUserId().equals(userId)) {
                noticeRepository.delete(notice);
                return "공지 삭제 완료!";
            } else {
                return "강사는 본인 작성한 공지만 삭제할 수 있습니다.";
            }
        }
        return "삭제 권한이 없습니다.";
    }

    // 조회수 증가 (null 안전)
    @Transactional
    public void increaseHit(Long id) {
        Notice notice = noticeRepository.findById(id).orElse(null);
        if (notice != null) {
            int cur = (notice.getHit() != null) ? notice.getHit() : 0;
            notice.setHit(cur + 1);
          //JPA dirty checking으로 자동 반영
        }
    }

}
