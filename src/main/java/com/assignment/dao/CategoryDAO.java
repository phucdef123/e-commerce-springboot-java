package com.assignment.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.entity.Category;

@Repository
public interface CategoryDAO extends JpaRepository<Category, Integer>{
	//List<Category> findByUnitPrice(double min, double max);
}
