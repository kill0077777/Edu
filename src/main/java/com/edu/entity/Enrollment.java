package com.edu.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "enrollment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Enrollment {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long enrollmentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Member user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	private LocalDateTime enrolledAt;

	private Integer progress; // 0~100
	private Boolean completedFlag; // true: 완료

	@PrePersist
	public void prePersist() {
		if (enrolledAt == null) {
			enrolledAt = LocalDateTime.now();
		}
		if (progress == null) {
			progress = 0;
		}
		if (completedFlag == null) {
			completedFlag = false;
		}
	}
}
