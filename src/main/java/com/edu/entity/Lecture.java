package com.edu.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lecture {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lectureId;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "instructor_id", referencedColumnName = "memberId", nullable = false)
	private Member instructor;

	@Column(length = 300)
	private String thumbnail;

	@Builder.Default
	@ElementCollection
	@CollectionTable(name="lecture_thumbnails", joinColumns=@JoinColumn(name="lecture_id"))
	@Column(name="thumbnail_path")
	private List<String> thumbnails = new ArrayList<>();

	@Column(updatable = false)
	@org.hibernate.annotations.CreationTimestamp
	private LocalDateTime regDate;

	@Column(nullable = false)
	private Integer price = 0;

	@Column(length = 50)
	private String category;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private LectureStatus status = LectureStatus.OPEN;

	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<LectureContent> contentList = new ArrayList<>();
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Review> reviewList = new ArrayList<>();
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Question> questionList = new ArrayList<>();
	@ManyToOne
    private Member member; // <-- 이 필드가 있어야 member 조건 쓸 수 있음

	// 사용 예시
	public String getMemberName() {
	    return member != null ? member.getName() : "";
	}

	
}
