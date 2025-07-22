package com.edu.dto;

import lombok.Data;

@Data
public class QuestionFormDto {
    private Long questionId;       // 수정시 필요
    private Long lectureId;        // 질문 대상 강의
    private String userId;           // 작성자(userId, PK)
    private String title;          // 질문 제목
    private String questionText;   // 질문 내용 (content/questionText 중 1개만)
    private String writerName;     // (화면 출력용) 작성자 이름, 필요시
}