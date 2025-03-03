package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.assignment.entity.User;

public interface UserDAO extends JpaRepository<User, String>{
	User findByEmail(String email);
}
