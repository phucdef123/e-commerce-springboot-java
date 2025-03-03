package com.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.assignment.entity.User;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	@Autowired
	SessionService session;

	@PostConstruct
	public void init() {
		System.out.println("✅ AuthInterceptor has been initialized!");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String uri = request.getRequestURI();
		session.set("securityUri", uri);
		User user = (User) session.get("user");

		System.out.println("🔍 Checking access for URI: " + uri);

		if (user == null) {
			System.out.println("❌ User not logged in. Redirecting to /auth/login");
			//response.sendRedirect("/");
			response.sendRedirect("/?loginRequired=true");
			return false;
		}
		if (uri.startsWith("/admin") && !user.isAdmin()) { // không phải admin
			System.out.println("❌ User is not admin. Redirecting to /auth/login");
			//session.remove("user");
			session.remove("securityUri");
			response.sendRedirect("/");
			return false;
		}
		System.out.println("✅ Access granted for URI: " + uri);
		return true;
	}
}

