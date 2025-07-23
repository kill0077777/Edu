package com.edu.lecture;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.edu.dto.LectureFormDto;
import com.edu.entity.Lecture;
import com.edu.entity.LectureStatus;
import com.edu.entity.Member;
import com.edu.entity.Review;
import com.edu.entity.Role;
import com.edu.member.MemberRepository;
import com.edu.member.MemberService;
import com.edu.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureService {
	//@Autowired
    private final LectureRepository lectureRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public Lecture getLecture(Long id) {
        return lectureRepository.findById(id).orElse(null);
    }

    public boolean isLectureStudent(Long lectureId, String userId) {
        // 예시: LectureStudent 엔티티/테이블을 이용, 수강생이면 true
        return lectureRepository.existsByLectureIdAndMember_UserId(lectureId, userId);
    }
    public Page<Lecture> findAll(PageRequest pageable) { return lectureRepository.findAll(pageable); }
    public Optional<Lecture> findById(Long lectureId) { return lectureRepository.findById(lectureId); }
    public List<Lecture> findByStatus(LectureStatus status) {
        return lectureRepository.findByStatus(status);
    }
    public List<Lecture> findByInstructorMemberId(Long memberId) {
        return lectureRepository.findByInstructor_MemberId(memberId);
    }
    public List<Lecture> findByInstructorUserId(String userId) {
        return lectureRepository.findByInstructor_UserId(userId);
    }
    public Lecture save(Lecture lecture) { return lectureRepository.save(lecture); }
    public boolean delete(Long id) {
        if (lectureRepository.existsById(id)) {
            lectureRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public void saveFromDto(LectureFormDto dto, Member loginUser) {
        Lecture lecture;
        if (dto.getLectureId() != null) {
            lecture = lectureRepository.findById(dto.getLectureId())
                        .orElseThrow(() -> new IllegalArgumentException("강의 없음"));
            lecture.setTitle(dto.getTitle());
            lecture.setDescription(dto.getDescription());
            lecture.setCategory(dto.getCategory());
            lecture.setPrice(dto.getPrice());
            // status 변환
            if (dto.getStatus() != null) {
                lecture.setStatus(LectureStatus.valueOf(dto.getStatus()));
            }
            if (loginUser.getRole() == Role.ADMIN && dto.getInstructorId() != null) {
                Member instructor = memberService.findById(dto.getInstructorId()).orElse(null);
                lecture.setInstructor(instructor);
            }
            // 썸네일 등...
        } else {
            Member instructor = loginUser.getRole() == Role.INSTRUCTOR
                ? loginUser
                : memberService.findById(dto.getInstructorId()).orElse(null);
            lecture = Lecture.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .status(LectureStatus.valueOf(dto.getStatus()))
                .instructor(instructor)
                .build();
        }
        lectureRepository.save(lecture);
    }


    public LectureFormDto getFormDto(Long lectureId) {
        return lectureRepository.findById(lectureId)
            .map(this::toFormDto)
            .orElse(new LectureFormDto());
    }
    public LectureFormDto toFormDto(Lecture entity) {
        LectureFormDto lectureFormDto = new LectureFormDto();
        lectureFormDto.setLectureId(entity.getLectureId());
        lectureFormDto.setTitle(entity.getTitle());
        lectureFormDto.setDescription(entity.getDescription());
        lectureFormDto.setCategory(entity.getCategory());
        lectureFormDto.setPrice(entity.getPrice());
        lectureFormDto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : "OPEN");
        lectureFormDto.setUserId(entity.getInstructor() != null ? entity.getInstructor().getUserId() : null);
        lectureFormDto.setInstructorId(entity.getInstructor() != null ? entity.getInstructor().getMemberId() : null);
        lectureFormDto.setThumbnailPath(entity.getThumbnail());
        lectureFormDto.setThumbnailPaths(entity.getThumbnails());
        return lectureFormDto;
    }
    public List<Lecture> getLecturesForReview() {
        return lectureRepository.findByStatusIn(
            Arrays.asList(LectureStatus.OPEN, LectureStatus.READY, LectureStatus.CLOSED, LectureStatus.END));
    }
    public List<Lecture> getLecturesForQna() {
        return lectureRepository.findAllForQna();
    }

	public List<Review> findByLecture_LectureId(Long lectureId) {
		return reviewRepository.findByLecture_LectureId(lectureId);
	}

	public boolean existsById(Long lectureId) {
	    return lectureRepository.existsById(lectureId);
	}
	public List<Member> findAllInstructors() {
	    return memberRepository.findByRole(Role.INSTRUCTOR);
	}

	public Optional<Member> findByUserId(String userId) {
	    return memberRepository.findByUserId(userId);
	}
}
