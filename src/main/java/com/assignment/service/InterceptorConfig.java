package com.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
	@Autowired
	AuthInterceptor authInterceptor;

//	@PostConstruct
//	public void init() {
//		System.out.println("✅ InterceptorConfig has been initialized!");
//	}

	@Autowired
    LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor).addPathPatterns("/admin/**");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/admin/**", "/account/**", "/order/**");
                //.excludePathPatterns("/admin/home");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }
}

