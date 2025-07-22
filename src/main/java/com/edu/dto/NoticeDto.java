package com.edu.dto;

import java.time.LocalDateTime;

import com.edu.entity.Notice;

import lombok.Data;

@Data
public class NoticeDto {
	private Long noticeId;
	private String title;
	private String content;
	private Long writerId;
	private String writerName;
	private String writerUserId; // ✅ Member.userId(로그인ID, String)
	private String writer;   // <= 이 필드가 반드시 있어야 함!
	private LocalDateTime regDate;
	private Integer hit;
	private Boolean fixedFlag;
	private String status; // "OPEN", "CLOSED" 등

	public static NoticeDto fromEntity(Notice notice) {
		NoticeDto noticeDto = new NoticeDto();
		noticeDto.setNoticeId(notice.getNoticeId());
		noticeDto.setTitle(notice.getTitle());
		noticeDto.setContent(notice.getContent());
		noticeDto.setRegDate(notice.getRegDate());
		noticeDto.setHit(notice.getHit());
		noticeDto.setFixedFlag(notice.getFixedFlag());
		if (notice.getWriter() != null) {
			noticeDto.setWriterId(notice.getWriter().getMemberId());
			noticeDto.setWriterName(notice.getWriter().getName());
			noticeDto.setWriterUserId(notice.getWriter().getUserId()); // 추가!
		}
		return noticeDto;
	}

	// JPQL DTO 생성자용
	public NoticeDto(Long noticeId, String title, String content, Long writerId, String writerName, String writerUserId,
					 LocalDateTime regDate, Integer hit, Boolean fixedFlag) {
		this.noticeId = noticeId;
		this.title = title;
		this.content = content;
		this.writerId = writerId;
		this.writerName = writerName;
		this.writerUserId = writerUserId;
		this.regDate = regDate;
		this.hit = hit;
		this.fixedFlag = fixedFlag;
	}

    public NoticeDto() {}
    // ... fromEntity 등 기타 메서드는 그대로

}
