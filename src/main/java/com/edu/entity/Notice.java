package com.edu.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notice")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notice {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long noticeId;

	private String title;

	@Lob
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id")
	private Member writer;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime regDate;

	private Integer hit = 0;

	private Boolean fixedFlag = false;  // TINYINT(0/1) -> Boolean

	//@Enumerated(EnumType.STRING)
	//@Column(nullable = false, length = 20)
	//private LectureStatus lectureStatus = LectureStatus.OPEN;

	@Column(nullable = false)
	private boolean isPresent = false; // ← 기본값 명시해두면 좋음!

	@ManyToOne
    private Member member; // <-- 이 필드가 있어야 member 조건 쓸 수 있음
}