package com.edu.enrollment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.edu.entity.Enrollment;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
	Optional<Enrollment> findByLecture_LectureIdAndMember_UserId(Long lectureId, String userId);
	 // user -> member로 반드시 수정!
    boolean existsByMember_UserIdAndLecture_LectureId(String userId, Long lectureId);
    //boolean existsByUser_UserIdAndLecture_LectureId(String userId, Long lectureId);
    boolean existsByLecture_LectureIdAndMember_UserId(Long lectureId, String userId);

	List<Enrollment> findByMember_UserId(String userId);

	
}
