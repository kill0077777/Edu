// src/main/java/com/edu/answer/AnswerController.java
package com.edu.answer;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.edu.dto.AnswerFormDto;
import com.edu.entity.Answer;
import com.edu.entity.Member;
import com.edu.entity.Question;
import com.edu.member.MemberRepository;
import com.edu.question.QuestionService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/answer")
public class AnswerController {
    private final AnswerService answerService;
    private final QuestionService questionService;
    private final MemberRepository memberRepository;

    @GetMapping("/list")
    public String list(Model model) {
        List<Answer> answerList = answerService.findAll();
        model.addAttribute("answerList", answerList);
        return "answer/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        Answer answer = answerService.findById(id).orElse(null);
        model.addAttribute("answer", answer);
        return "answer/detail";
    }

    @GetMapping({"/form", "/edit/{id}"})
    public String form(
            @RequestParam(value = "questionId", required = false) Long questionId,
            @PathVariable(value = "id", required = false) Long id,
            Model model) {

        AnswerFormDto answerFormDto = new AnswerFormDto();

        if (id != null) {
            Answer answer = answerService.findById(id).orElseThrow();
            answerFormDto.setAnswerId(answer.getAnswerId());
            answerFormDto.setQuestionId(answer.getQuestion().getQuestionId());
            answerFormDto.setAnswerText(answer.getAnswerText());
            answerFormDto.setMemberId(answer.getMember().getMemberId());
            questionId = answer.getQuestion().getQuestionId();
        } else if (questionId != null) {
            answerFormDto.setQuestionId(questionId);
        }

        Question question = questionService.findById(questionId).orElseThrow();
        model.addAttribute("questionText", question.getQuestionText());

        List<Member> adminList = memberRepository.findByRole("ADMIN");
        model.addAttribute("adminList", adminList);

        model.addAttribute("answerFormDto", answerFormDto);
        return "answer/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("answerFormDto") AnswerFormDto answerFormDto) {
        answerService.saveFormDto(answerFormDto);
        return "redirect:/answer/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        answerService.delete(id);
        return "redirect:/answer/list";
    }
}
