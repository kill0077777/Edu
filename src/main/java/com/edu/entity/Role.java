package com.edu.entity;

import lombok.Getter;

@Getter
public enum Role {
	ADMIN("ROLE_ADMIN"),
	INSTRUCTOR("ROLE_INSTRUCTOR"),
	STUDENT("ROLE_STUDENT");

	private final String value;

	Role(String value) {
		this.value = value;
	}

	/*
		Spring Security용 권한 문자열 반환.
		ex) ROLE_ADMIN, ROLE_INSTRUCTOR
	*/
	public String getAuthority() {
		return value;
	}
	
	//ADMIN, INSTRUCTOR, STUDENT;

}
