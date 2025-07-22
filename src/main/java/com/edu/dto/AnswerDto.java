package com.edu.dto;

import java.time.LocalDateTime;

import com.edu.entity.Answer;

import lombok.Data;

@Data
public class AnswerDto {
    private Long answerId;
    private Long questionId;
    private Long memberId;
    private String userId;
    private String memberName; // 필요시
    private String answerText;
    private LocalDateTime createdAt;

    // 엔티티 → DTO 변환 메서드
    public static AnswerDto fromEntity(Answer answer) {
        if (answer == null) {
			return null;
		}
        AnswerDto answerDto = new AnswerDto();
        answerDto.setAnswerId(answer.getAnswerId());
        answerDto.setAnswerText(answer.getAnswerText());
        answerDto.setCreatedAt(answer.getCreatedAt());
        // Question 참조
        if (answer.getQuestion() != null) {
        	answerDto.setQuestionId(answer.getQuestion().getQuestionId());
        }
        // Member(작성자) 참조
        if (answer.getMember() != null) {
        	answerDto.setMemberId(answer.getMember().getMemberId());
        	answerDto.setUserId(answer.getMember().getUserId());
        	answerDto.setMemberName(answer.getMember().getName());
        }
        return answerDto;
    }


}
