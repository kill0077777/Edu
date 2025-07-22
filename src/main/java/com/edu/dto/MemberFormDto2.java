package com.edu.dto;

import com.edu.entity.Member;
import com.edu.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberFormDto2 {  //회원(Member) DTO

	public MemberFormDto2() {} // 반드시 필요!
	private Long memberId;

	@Size(min = 5, max = 25)
    @NotEmpty(message = "사용자ID는 필수항목입니다.")
	private String userId;

	@NotEmpty(message = "비밀번호는 필수항목입니다.")
	private String password;

	@NotEmpty(message = "이름는 필수항목입니다.")
	private String name;

	@NotEmpty(message = "이메일은 필수항목입니다.")
	@Email
	private String email;

	//public static Object Role;  // "ADMIN", "INSTRUCTOR", "STUDENT"

	private String role;
	private String regDate;
	public Object get;

	// 🔽 바로 아래에 toEntity() 메서드 추가!
    public Member toEntity() {
        Member member = new Member();
        member.setUserId(this.userId);
        member.setPassword(this.password);
        member.setName(this.name);
        member.setEmail(this.email);
        member.setRole(Role.valueOf(this.role)); // role이 enum이면 변환 필요!
        // 기타 필요한 필드 매핑
        return member;
    }

 // 반드시 이 생성자가 필요!!
    public MemberFormDto2(String userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }


}
