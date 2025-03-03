package com.assignment.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assignment.entity.Product;

public interface ProductDAO extends JpaRepository<Product, String>{
	@Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = ?1")
	long countByCategoryId(Integer categoryId);

	List<Product> findAllByOrderByRegisterDateDesc();
	Page<Product> findByCategoryCategoryId(Long categoryId, Pageable pageable);
	Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);

}
