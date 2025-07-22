package com.edu.entity;

import java.time.LocalDateTime;
//import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionId;

	private String title;
    private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false, length = 1000)
	private String questionText;

	@Column(name = "created_at",  columnDefinition = "datetime default current_timestamp")
	//private Timestamp createdAt;
	private LocalDateTime createdAt;


	@OneToMany(mappedBy = "question")
	private List<Answer> answerList; // 필드가 반드시 있어야 함!

	
}
