package com.edu.enrollment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.edu.entity.Enrollment;
import com.edu.entity.Lecture;
import com.edu.entity.Member;
import com.edu.lecture.LectureRepository;
import com.edu.member.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    /** 수강신청 (중복방지) */
    public Enrollment enroll(Long lectureId, String userId) {
        Optional<Member> memberOpt = memberRepository.findByUserId(userId);
        Optional<Lecture> lectureOpt = lectureRepository.findById(lectureId);

        if (memberOpt.isEmpty() || lectureOpt.isEmpty()) {
			throw new IllegalArgumentException("수강신청 실패");
		}

        // 이미 수강내역 있는지 체크
        if (enrollmentRepository.existsByLecture_LectureIdAndMember_UserId(lectureId, userId)) {
			throw new IllegalStateException("이미 수강 중");
		}

        Enrollment enroll = Enrollment.builder()
                .lecture(lectureOpt.get())
                .member(memberOpt.get())
                .progress(0)
                .completedFlag(false)
                .build();
        return enrollmentRepository.save(enroll);
    }

    /** 수강생 여부 (Controller에서 상태 뷰 표기용) */
    public boolean isStudent(Long lectureId, String userId) {
        return enrollmentRepository.existsByLecture_LectureIdAndMember_UserId(lectureId, userId);
    }

    /** 특정 강의 수강생의 수강상태 조회 (없으면 null) */
    public Optional<Enrollment> getEnrollment(Long lectureId, String userId) {
        return enrollmentRepository.findByLecture_LectureIdAndMember_UserId(lectureId, userId);
    }

    /** 진행률 및 완료여부 업데이트 */
    public void updateProgress(Long enrollmentId, int progress) {
        Enrollment e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("수강이력 없음"));
        e.setProgress(progress);
        e.setCompletedFlag(progress >= 100);
        // save 불필요 (변경감지, 트랜잭션 내라면)
        enrollmentRepository.save(e);
    }

    /** 내 모든 수강이력 */
    public List<Enrollment> getMyEnrollments(String userId) {
        return enrollmentRepository.findByMember_UserId(userId);
    }

    /** 강의별/회원별 상태를 Status 문자열로 반환(뷰에서 활용) */
    public String getStatusForLecture(Long lectureId, String userId) {
        // 비로그인
        if (userId == null) {
			return "OPEN";
		}
        Optional<Enrollment> opt = enrollmentRepository.findByLecture_LectureIdAndMember_UserId(lectureId, userId);
        if (opt.isEmpty()) {
			return "OPEN";
		}
        int progress = opt.get().getProgress();
        if (progress == 0) {
			return "OPEN";
		}
        if (progress == 100) {
			return "CLOSED";
		}
        return "READY";
    }

    public List<Enrollment> myEnrollments(String loginUserId) {
        return enrollmentRepository.findByMember_UserId(loginUserId);
    }

    public boolean isEnrolled(String userId, Long lectureId) {
    	// userId가 lectureId에 수강신청했는지 여부
        return enrollmentRepository.existsByMember_UserIdAndLecture_LectureId(userId, lectureId);
    }
}

