// src/main/java/com/edu/answer/AnswerService.java
package com.edu.answer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edu.dto.AnswerFormDto;
import com.edu.entity.Answer;
import com.edu.entity.Member;
import com.edu.entity.Question;
import com.edu.member.MemberRepository;
import com.edu.question.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;

    public List<Answer> findAll() {
        return answerRepository.findAll();
    }

    public Optional<Answer> findById(Long id) {
        return answerRepository.findById(id);
    }

    public void saveFormDto(AnswerFormDto dto) {
        Member member = memberRepository.findById(dto.getMemberId())
            .orElseThrow(() -> new RuntimeException("작성자(관리자) 없음"));
        Question question = questionRepository.findById(dto.getQuestionId())
            .orElseThrow(() -> new RuntimeException("질문 없음"));

        Answer answer;
        if (dto.getAnswerId() != null) {
            // 수정
            answer = answerRepository.findById(dto.getAnswerId()).orElseThrow();
            answer.setAnswerText(dto.getAnswerText());
            answer.setMember(member);
        } else {
            // 신규 등록
            answer = new Answer();
            answer.setQuestion(question);
            answer.setMember(member);
            answer.setAnswerText(dto.getAnswerText());
            answer.setCreatedAt(LocalDateTime.now());
        }
        answerRepository.save(answer);
    }

    public void delete(Long id) {
        answerRepository.deleteById(id);
    }
}
