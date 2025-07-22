package com.edu.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.edu.dto.QuestionFormDto;
import com.edu.entity.Lecture;
import com.edu.entity.Member;
import com.edu.entity.Question;
import com.edu.lecture.LectureRepository;
import com.edu.member.MemberRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    public Page<Question> findAll(Pageable pageable) { return questionRepository.findAll(pageable); }
    public Optional<Question> findById(Long id) { return questionRepository.findById(id); }
    public List<Question> findByLecture(Long lectureId) { return questionRepository.findByLecture_LectureId(lectureId); }
    public List<Question> findByUserId(String userId) { return questionRepository.findByMember_UserId(userId); }
    public Question save(Question question) { return questionRepository.save(question); }
    public void delete(Long id) { questionRepository.deleteById(id); }

    // 강의별 질문 리스트 가져오기
    public List<Question> getQuestionsByLectureId(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found: " + lectureId));
        return questionRepository.findByLecture(lecture);
    }

    public void saveFromDto(QuestionFormDto questionFormDto) {
        // 1. 강의, 작성자 조회
        Lecture lecture = lectureRepository.findById(questionFormDto.getLectureId())
            .orElseThrow(() -> new IllegalArgumentException("강의 정보 없음"));
        // DTO -> 엔티티 변환 (saveFromDto)
        Member member = memberRepository.findByUserId(questionFormDto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("작성자 정보 없음"));

        // 2. Question 엔티티 생성 및 값 세팅
        Question question = new Question();

        question.setLecture(lecture);
        question.setMember(member);
        question.setQuestionText(questionFormDto.getQuestionText());
        question.setCreatedAt(LocalDateTime.now());

        // 3. 저장
        questionRepository.save(question);
    }

    // [질문 작성/수정 폼에 보여줄 DTO 반환]
    public QuestionFormDto getFormDtoById(Long id) {
        Optional<Question> optional = questionRepository.findById(id);
        if (!optional.isPresent()) {
            return null; // 또는 throw new EntityNotFoundException();
        }
        Question question = optional.get();

        // 엔티티 -> DTO 변환
        QuestionFormDto questionFormDto = new QuestionFormDto();
        questionFormDto.setTitle(question.getTitle());
        questionFormDto.setQuestionText(question.getQuestionText());
        // Long userId로 맞춰야 함
        questionFormDto.setUserId(question.getMember() != null ? question.getMember().getUserId() : null);
        questionFormDto.setLectureId(
            question.getLecture() != null ? question.getLecture().getLectureId() : null);
        return questionFormDto;
    }


    public Page<Question> findByLecturePaged(Long lectureId, Pageable pageable) {
        return questionRepository.findByLecture_LectureId(lectureId, pageable);
    }

}