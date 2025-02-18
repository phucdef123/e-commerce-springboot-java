package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.User;

public interface UserDAO extends JpaRepository<User, String>{

}
