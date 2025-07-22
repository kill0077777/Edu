package com.edu.dto;

import lombok.Data;

@Data
public class LectureListDto {  //목록(List) 뷰 최적화(빠르고 가볍게)
	private Long lectureId;
	private String userId;
	private String title;
	private String instructorName;
	private String category;
	private Integer price;
	private String status;
	private String thumbnail;
}