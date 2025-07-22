package com.edu.question;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.edu.dto.QuestionDto;
import com.edu.dto.QuestionFormDto;
import com.edu.entity.Lecture;
import com.edu.entity.Question;
import com.edu.lecture.LectureService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("/question")
public class QuestionController {
	private final QuestionService questionService;
	private final LectureService lectureService;

	//http://localhost:80/question/list
	@GetMapping("/list")
	public String list( @RequestParam(value = "page", defaultValue = "1") int page,
						@RequestParam(value = "size", defaultValue = "10") int size,
						Model model) {
		 Page<Question> questionPage = questionService.findAll(PageRequest.of(page - 1, size));
		 model.addAttribute("questionPage", questionPage);
		 return "question/list";
	}

	@GetMapping("/{lectureId}/question")
	public String questionListByLecture(
	    @PathVariable("lectureId") Long lectureId,
	    @RequestParam(value = "page", defaultValue = "1") int page,
	    @RequestParam(value = "size", defaultValue = "10") int size,
	    Model model) {

	    Page<Question> questionPage = questionService.findByLecturePaged(lectureId, PageRequest.of(page - 1, size));
	    // Question -> DTO 변환
	    List<QuestionDto> questionDtoList = questionPage.stream().map(QuestionDto::fromEntity).toList();
	    Page<QuestionDto> questionDtoPage = new PageImpl<>(questionDtoList, questionPage.getPageable(), questionPage.getTotalElements());
	    model.addAttribute("questionPage", questionDtoPage);  // 템플릿에서는 questionPage.content 사용!
	    model.addAttribute("lectureId", lectureId);
	    return "question/list"; // ex) question/list.html
	}

	//상세페이지
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") Long id, Model model) {
	    Question question = questionService.findById(id).orElse(null);
	    if (question == null) {
	        model.addAttribute("error", "해당 질문을 찾을 수 없습니다.");
	        return "error/404";
	    }
	    QuestionDto questionDto = QuestionDto.fromEntity(question);
	    model.addAttribute("question", questionDto);
	    return "question/detail";
	}


    // [질문 폼 (신규/수정)]
	@GetMapping({"/form", "/edit/{id}"})
	public String questionForm(
	        Model model,
	        @PathVariable(value = "id", required = false) Long id,
	        @RequestParam(value = "lectureId", required = false) Long lectureId) {

	    List<Lecture> lectureList = lectureService.getLecturesForQna();
	    model.addAttribute("lectureList", lectureList);

	    QuestionFormDto questionFormDto;
	    if (id != null) {
	        questionFormDto = questionService.getFormDtoById(id);
	        if (questionFormDto == null) {
	            // 질문이 없을 때 예외 처리(선택)
	            throw new IllegalArgumentException("해당 질문이 존재하지 않습니다.");
	        }
	    } else {
	        questionFormDto = new QuestionFormDto();
	        // 여기서 강의 존재 여부 체크!
	        if (lectureId != null) {
	            // ⭐ 여기서 체크!
	            if (!lectureService.existsById(lectureId)) {
	                throw new IllegalArgumentException("해당 강의가 존재하지 않습니다.");
	            }
	            questionFormDto.setLectureId(lectureId);
	        }
	    }
	    model.addAttribute("questionFormDto", questionFormDto);

	    return "question/form";
	}


    @PostMapping("/save")
    public String saveQuestion(
        @ModelAttribute QuestionFormDto questionFormDto,
        @RequestParam("userId") String userId,
        Model model
    ) {
        questionFormDto.setUserId(userId);   // DTO에도 Long userId로
        questionService.saveFromDto(questionFormDto);
        return "redirect:/question/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        questionService.delete(id);
        return "redirect:/question/list";
    }

}
