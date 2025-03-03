package com.assignment.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.entity.Cart;

@Repository
public interface CartDAO extends JpaRepository<Cart, String>{
	List<Cart> findByUserUsername(String username);
	Cart findByUserUsernameAndProductId(String username, String productId);
	void deleteByUserUsername(String username);
}

