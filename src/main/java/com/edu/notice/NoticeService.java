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
				.stream().map(notice -> NoticeDto.fromEntity(notice)).collect(Collectors.toList());
    }

	// 일반(비고정) 공지 (페이징)
    // Page<Notice> → Page<NoticeDto> 변환 (DTO 변환에 fromEntity 사용)
    public Page<NoticeDto> getGeneralNotices(Pageable pageable) {
        return noticeRepository.findByFixedFlagFalseOrderByNoticeIdDesc(pageable)
        			.map(notice -> NoticeDto.fromEntity(notice));
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
        return noticeRepository.save(notice);
    }

    // 삭제
    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    // 조회수 증가
    @Transactional
    public void increaseHit(Long id) {
        Notice notice = noticeRepository.findById(id).orElse(null);
        if (notice != null) {
            notice.setHit(notice.getHit() + 1);
            // JPA dirty checking으로 자동 반영
        }
    }
}
