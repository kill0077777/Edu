package com.edu.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoticeDetailDto {
	private Long noticeId;
	private String title;
	private String content; // 상세이므로 본문 포함
	private Long writerId;
	private String writerName;
	private LocalDateTime regDate;
	private Integer hit;
	private Boolean fixedFlag;
	private String status;
}