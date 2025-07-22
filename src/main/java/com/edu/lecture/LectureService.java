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
import com.edu.entity.Review;
import com.edu.review.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureService {
	//@Autowired
    private final LectureRepository lectureRepository;
    private final ReviewRepository reviewRepository;

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
    public Lecture saveFromDto(LectureFormDto lectureFormDto) {
        // lectureFormDto → entity 변환, 예시 (상세 변환은 lectureFormDto/엔티티 구조에 맞게!)
        Lecture entity = Lecture.builder()
                .lectureId(lectureFormDto.getLectureId())
                .title(lectureFormDto.getTitle())
                .description(lectureFormDto.getDescription())
                .category(lectureFormDto.getCategory())
                .price(lectureFormDto.getPrice())
                .status(LectureStatus.valueOf(lectureFormDto.getStatus()))
                // .instructor(실제 Instructor Member 설정)
                .build();
        return lectureRepository.save(entity);
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
}
