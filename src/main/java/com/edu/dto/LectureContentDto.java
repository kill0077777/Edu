package com.edu.dto;

import lombok.Data;

@Data
public class LectureContentDto { //강의자료(LectureContent) DTO
    private Long contentId;
    private Long lectureId;
    private String userId;       // 회원 로그인 아이디 (email 등)
    private String contentType; // "VIDEO", "PDF", "IMAGE", "DOC"
    private String fileUrl;
    private Integer orderNo;
    private String regDate;
}
