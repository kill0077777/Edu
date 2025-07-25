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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enrollment_id") // <- 꼭 명시!
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private Boolean completedFlag; // ★ 완료여부 추가! (nullable=true도 무방)
    private boolean paid; // 결제여부 (무료일때도 true로 처리)
    private int progress; // 0~100
    private LocalDateTime enrollDate;

    @Column(length = 20)
    private String status; // "READY"(수강중), "CLOSED"(완료), "END"(강의종료)

    // 상태 계산 예시: setStatus에 따라 자동 설정 가능
    public void updateStatusByProgress() {
        if (progress >= 100) this.status = "CLOSED";
        else if (progress > 0) this.status = "READY";
        else this.status = "READY";
    }
}

