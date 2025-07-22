package com.edu.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LectureDto { // 강의(Lecture) DTO
	private Long lectureId;
	private String userId;
	private String title;
	private String description;
	private Long instructorId;  // 연관키: 강사(member_id)
	private String instructorName; // 필요시
	private String thumbnail;
	private LocalDateTime regDate;
	private Integer price;
	private String status;  // "READY", "OPEN", "CLOSED", "END"
	private String category;


}