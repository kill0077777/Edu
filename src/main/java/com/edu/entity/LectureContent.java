package com.edu.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lecture_content")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LectureContent {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lecture_id", nullable = false)
	private Lecture lecture;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ContentType contentType;

	@Column(nullable = false, length = 300)
	private String fileUrl;

	@Column
	private Integer orderNo = 1;

	@Column(columnDefinition = "datetime default current_timestamp")
	private java.sql.Timestamp regDate;

	public enum ContentType {
		VIDEO, PDF, IMAGE, DOC
	}

	@ManyToOne
    private Member member; // <-- 이 필드가 있어야 member 조건 쓸 수 있음
}
