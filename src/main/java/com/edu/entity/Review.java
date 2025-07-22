package com.edu.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "review")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "user_id")
    private Member member;

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(nullable = false)
    private int rating;

    @Column(length = 1000)
    private String comment;

    @Column(columnDefinition = "datetime default current_timestamp")
    private LocalDateTime regDate;
	//private java.sql.Timestamp regDate;

    @Column(name = "created_at",  columnDefinition = "datetime default current_timestamp")
	private LocalDateTime createdAt;
	//private Timestamp createdAt;

	// 👉👉 바로 아래처럼 추가! (클래스 맨 아래에 두는 게 일반적)
	public String getMemberName() {
		return member != null ? member.getName() : "";
	}

	private String title;
	private String content;



}
