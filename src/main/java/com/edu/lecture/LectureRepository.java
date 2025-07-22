package com.edu.lecture;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.edu.entity.Lecture;
import com.edu.entity.LectureStatus;
public interface LectureRepository extends JpaRepository<Lecture, Long> {
	@Override
	Page<Lecture> findAll(Pageable pageable);
    @Query("SELECT l FROM Lecture l WHERE l.status IN ('OPEN','READY','CLOSED','END')")
    List<Lecture> findAllForReview();
    @Query("SELECT l FROM Lecture l WHERE l.status IN ('OPEN','READY','CLOSED')")
    List<Lecture> findAllForQna();

    // 위에서 'Member'가 실제로 연관관계 필드로 있고, Member 엔티티에 userId 필드가 있을 때만 가능
    List<Lecture> findByInstructorUserId(String userId);

    List<Lecture> findByStatus(LectureStatus status);
    List<Lecture> findByStatusIn(List<LectureStatus> statusList);
    List<Lecture> findByInstructor_MemberId(Long memberId);
    List<Lecture> findByInstructor_UserId(String userId);
    // 1. lectureId만 기준으로 체크하고 싶으면
    boolean existsByLectureId(Long lectureId);

    // 2. 만약 Lecture 엔티티에 member (예: 강사) 연관관계가 있다면
    boolean existsByLectureIdAndMember_UserId(Long lectureId, String userId);

    @Override
	boolean existsById(Long lectureId);
}
