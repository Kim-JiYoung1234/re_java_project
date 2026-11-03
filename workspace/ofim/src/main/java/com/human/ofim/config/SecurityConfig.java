package com.human.ofim.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// 암호화 빈 등록
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 1. CSRF 비활성화 (개발 단계에서의 POST 요청 차단 방지)
				.csrf(csrf -> csrf.disable())

				// 2. 채팅창(iframe) 열기 설정: sameOrigin에서만 프레임 노출 허용
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

				// 3. 권한 설정
				.authorizeHttpRequests(auth -> auth
						// 모든 정적 리소스와 공개 URL 허용
						.requestMatchers("/css/**", "/js/**", "/images/**", "/auth/**", "/fitness/**", "/api/**", "/")
						.permitAll().anyRequest().permitAll())

				// 4. Spring Security 기본 로그인 폼 가로채기 방지 (커스텀 로그인 사용)
				.formLogin(form -> form.disable())

				// 5. 🌟 로그아웃 설정: 현재 페이지 유지 로직 포함
				.logout(logout -> logout.logoutUrl("/logout")
						.logoutSuccessHandler((request, response, authentication) -> {
							// 요청 파라미터에서 redirect 주소를 읽어옴
							String redirectUrl = request.getParameter("redirect");

							// 주소가 있으면 그곳으로, 없으면 /home으로 리다이렉트
							if (redirectUrl != null && !redirectUrl.isEmpty()) {
								response.sendRedirect(redirectUrl);
							} else {
								response.sendRedirect("/home");
							}
						}).invalidateHttpSession(true) // 세션 무효화
						.deleteCookies("JSESSIONID") // 쿠키 삭제
						.permitAll());

		return http.build();
	}
}