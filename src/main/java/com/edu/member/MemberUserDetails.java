package com.edu.member;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.edu.entity.Member;

public class MemberUserDetails implements UserDetails {
    private final Member member;


    public MemberUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public String getUsername() { return member.getUserId(); }
    public String getUserId() { return member.getUserId(); }   // 부가적으로 추가 OK

    @Override
    public String getPassword() { return member.getPassword(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role이 ENUM이면 .name(), String이면 그대로
        // 예: ROLE_ADMIN, ROLE_STUDENT, ROLE_INSTRUCTOR
        return List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
        // 만약 String이면: new SimpleGrantedAuthority("ROLE_" + member.getRole())
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }

    // 추가적으로 Member 객체 전체가 필요하다면 Getter로 꺼낼 수도 있음
    public Member getMember() {
        return member;
    }
}
