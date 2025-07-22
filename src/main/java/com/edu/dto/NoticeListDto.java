package com.edu.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoticeListDto {
	private Long noticeId;
	private String title;
	private Long writerId;
	private String writerName;
	private LocalDateTime regDate;
	private Integer hit;
	private Boolean fixedFlag;
	private String status;
}