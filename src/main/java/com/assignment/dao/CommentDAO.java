package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.Comment;

public interface CommentDAO extends JpaRepository<Comment, String>{

}
