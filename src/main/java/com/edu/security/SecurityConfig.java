package com.edu.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private UserSecurityService userSecurityService;

	@Autowired
    private CustomLoginSuccessHandler successHandler;

	@Bean
	public SecurityFilterChain filterChain(
					HttpSecurity http,
					CustomAuthenticationProvider customAuthenticationProvider  // 여기에만 인자로 받기!
				) throws Exception {
		http
			.authenticationProvider(customAuthenticationProvider)  //  ★ 이 한 줄만 추가!  //  ← 여기서 커스텀 provider 등록;
			.userDetailsService(userSecurityService) // 커스텀 UserDetailsService 등록
			// [1] 권한 정책
			.authorizeHttpRequests(authorize -> authorize
			.requestMatchers("/", "/**").permitAll()
			.requestMatchers(
					"/css/**", "/js/**", "/images/**", "/bootstrap.min.css", "/style.css")
			.permitAll()
			.requestMatchers(
					"/", "/user/login", "/user/signup", "/member/form")
			.permitAll()
			.anyRequest().authenticated()
			)

			// [2] CSRF 보호 - MySQL8에는 별도 예외 필요 없음 (그냥 활성화)
			// .csrf(csrf -> csrf.disable()) // 개발 중에는 disable, 운영 시엔 생략/주석!

			// [3] X-Frame-Options 헤더 - H2 콘솔 안 쓰면 생략 가능
			// .headers(headers -> headers
			//     .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
			// )
			.formLogin(form -> form
				.loginPage("/member/login")        // 로그인 폼 URL (GET)
				.loginProcessingUrl("/member/login") // 로그인 폼 submit (POST)
				.defaultSuccessUrl("/", true)    // 로그인 성공시 이동
				//.failureUrl("/member/login?error") // 로그인 실패시 이동
				.usernameParameter("userId")   // 폼 input name
				.passwordParameter("password")
				.permitAll()
			)
			.logout(logout -> logout
				// .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout")) // 이건 주석 OK
				.logoutUrl("/member/logout")        // 로그아웃 요청 URL (POST 방식으로만 동작!)
				.logoutSuccessUrl("/")              // 로그아웃 후 이동할 경로
				.invalidateHttpSession(true)        // 세션 무효화
				.deleteCookies("JSESSIONID")        // (권장) 쿠키까지 삭제
				.permitAll()
				);


		return http.build();
	}


	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}


	// 커스텀 프로바이더도 등록해야 하는 경우 아래처럼 추가
	//@Bean
	//public AuthenticationManager authenticationManager(
	//       AuthenticationProvider authenticationProvider,
	//CustomAuthenticationProvider customProvider) throws Exception {
	//	return new ProviderManager(List.of(customProvider, authenticationProvider));
	//}
}
