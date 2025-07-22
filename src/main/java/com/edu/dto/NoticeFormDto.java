package com.edu.dto;

import com.edu.entity.Notice;

import lombok.Data;

@Data
public class NoticeFormDto {
	private Long noticeId;        // 수정 시 필요
	//private String userId;        // 작성자 (로그인 시 자동 세팅, 폼 입력 X)
	private String title;
	private String content;
	private Long writerId;         // 폼에선 Member 객체 대신 id로
	private Boolean fixedFlag = false; // 고정공지 여부(기본 false)
	private String status; // 혹은 Enum 타입이라면 Enum명

	public boolean isPresent;
	public Object get;
	
	// ---- 엔티티 → 폼 DTO 변환 ----
    public static NoticeFormDto fromEntity(Notice notice) {
        NoticeFormDto noticeFormDto = new NoticeFormDto();
        noticeFormDto.setNoticeId(notice.getNoticeId());
        noticeFormDto.setTitle(notice.getTitle());
        noticeFormDto.setContent(notice.getContent());
        noticeFormDto.setFixedFlag(notice.getFixedFlag());
        // 작성자 정보
        if (notice.getWriter() != null) {
        	noticeFormDto.setWriterId(notice.getWriter().getMemberId());
        }
        // 상태 (LectureStatus Enum이 있을 경우)
        //if (notice.getLectureStatus() != null) {
        //	noticeFormDto.setStatus(notice.getLectureStatus().name());
       // }
        return noticeFormDto;
    }
	
}
