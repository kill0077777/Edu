package com.edu.dto;

import lombok.Data;

@Data
public class EnrollmentDto { //수강신청(Enrollment) DTO
	private Long enrollmentId;  // 수강신청 ID
    private Long memberId;		// 회원 ID
    private Long lectureId;		// 강의 ID
    private String lectureTitle;
    private String userId;       // 회원 로그인 아이디 (email 등)
    private String enrolledAt;   // 날짜 (보통 String 또는 LocalDateTime)
    private Integer progress;
    private Boolean completedFlag;
}
