package com.edu.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.edu.entity.Question;

import lombok.Data;

@Data
public class QuestionDto { //질문(Question) DTO
	private MemberFormDto member; // 여기서는 DTO로
	private Long questionId;
	private Long lectureId;
	private Long memberId;
	private String userId;

	private String title;
	private String content;
	private String writer;

	private String memberName; // 필요시
	private String questionText;
	private LocalDateTime createdAt;
	private List<AnswerDto> answerList;


	public static QuestionDto fromEntity(Question question) {
	    QuestionDto questionDto = new QuestionDto();
	    questionDto.setQuestionId(question.getQuestionId());
	    questionDto.setQuestionText(question.getQuestionText());
	    questionDto.setCreatedAt(question.getCreatedAt());
	    if (question.getMember() != null) {
	    	questionDto.setMemberId(question.getMember().getMemberId());
	    	questionDto.setUserId(question.getMember().getUserId());
	    	questionDto.setMemberName(question.getMember().getName());
	    }
	    // answerList도 DTO 변환 (null 체크)
	    if (question.getAnswerList() != null) {
	    	questionDto.setAnswerList(
	            question.getAnswerList().stream().map(AnswerDto::fromEntity).collect(Collectors.toList())
	        );
	    }
	    return questionDto;
	}



}
