package com.edu.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AnswerFormDto {
    private Long answerId;     // 답변 PK (수정시 필요)
    private Long questionId;   // 어떤 질문에 대한 답변인지
    private Long memberId;     // 작성자(선택한 관리자) PK
    private String answerText; // 답변 내용
    private String memberName;
    private LocalDateTime  createdAt;
}
