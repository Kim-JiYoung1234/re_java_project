package com.human.ofim.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;
import java.util.concurrent.TimeUnit;

import com.human.ofim.interceptor.AdminInterceptor;
import com.human.ofim.filter.XssFilter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private AdminInterceptor adminInterceptor;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 관리자 차단 인터셉터
		registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin", "/admin/**")
				.excludePathPatterns("/admin/login", "/admin/logout, /auth/login");


	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploads/**")
				.addResourceLocations("file:///" + uploadDir)
				.setCacheControl(CacheControl.maxAge(0, TimeUnit.SECONDS).mustRevalidate()).resourceChain(true);
	}

	@Bean
	public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
		FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new XssFilter());
		registration.addUrlPatterns("/*");
		registration.setName("xssFilter");
		registration.setOrder(1);
		return registration;
	}
}