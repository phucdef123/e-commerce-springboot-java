package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.Product;

public interface ProductDAO extends JpaRepository<Product, String>{

}
