package com.assignment.service;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.assignment.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component  
public class LogInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) {
			System.out.println(request.getRequestURI() + ", " + new Date() + ", " + user.getFullname());
		} 
//			else {
//			System.out.println(request.getRequestURI() + ", " + new Date() + ", Guest User");
//		}
		return true;
	}
}

