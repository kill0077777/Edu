package com.edu.dto;

import lombok.Data;

@Data
public class MemberDto {  //회원(Member) DTO
    private Long memberId;
    private String userId;
    private String password;
    private String name;
    private String email;
    private String role; // "ADMIN", "INSTRUCTOR", "STUDENT"
    private String regDate;
}
