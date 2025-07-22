// LectureFormDto.java
package com.edu.dto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class LectureFormDto {
	private Long lectureId;
	private String userId;
	private String title;
	private String description;
	private Long instructorId;
	private String category;
	private Integer price;
	private String status; // "OPEN" 등, Enum.name() 그대로
	private String lectureStatus;
	private MultipartFile thumbnailFile; // 대표 썸네일
	private String thumbnailPath; // 대표 썸네일 경로
	private List<MultipartFile> thumbnailFiles; // 여러개 업로드용
	private List<String> thumbnailPaths; // 여러개 저장용
}
