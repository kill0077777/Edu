package com.edu.dto;

import java.time.LocalDateTime;

import com.edu.entity.Lecture;
import com.edu.entity.Member;
import com.edu.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberFormDto {

    private Long memberId;

    @Size(min = 5, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String userId;

    @NotEmpty(message = "비밀번호는 필수항목입니다.")
    private String password;

    @NotEmpty(message = "이름은 필수항목입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String email;

    // enum Role: "ADMIN", "INSTRUCTOR", "STUDENT"
    //@NotEmpty(message = "역할(ROLE)은 필수항목입니다.")
    private String role;

    // 등록일 (선택)
    private LocalDateTime regDate;

    // ⭐ 기본 생성자 필수
    public MemberFormDto() {}

    // 필요하다면 파라미터 생성자 (주로 테스트, 변환용)
    public MemberFormDto(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // 엔티티 변환 메서드
    public Member toEntity() {
        Member member = new Member();
        member.setUserId(this.userId);
        member.setPassword(this.password);
        member.setName(this.name);
        member.setEmail(this.email);
        // role이 String이면 enum 변환 필요
        if (this.role != null) {
            member.setRole(Role.valueOf(this.role));
        }
        member.setRegDate(this.regDate);
        // 기타 필드 매핑
        return member;
    }
    public static MemberFormDto fromEntity(Member member) {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setMemberId(member.getMemberId());
        memberFormDto.setUserId(member.getUserId());
        memberFormDto.setName(member.getName());
        memberFormDto.setEmail(member.getEmail());
        memberFormDto.setRole(member.getRole().name());
        // password는 안 넘김
        return memberFormDto;
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
}
