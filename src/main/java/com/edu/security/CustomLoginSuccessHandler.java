package com.edu.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.edu.member.MemberUserDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 로그인 성공 사용자 정보
        MemberUserDetails userDetails = (MemberUserDetails) authentication.getPrincipal();
        request.getSession().setAttribute("msg", "환영합니다, " + userDetails.getMember().getName() + "님!");
        response.sendRedirect("/");
    }
}