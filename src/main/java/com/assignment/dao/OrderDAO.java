package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.Order;

public interface OrderDAO extends JpaRepository<Order, String>{

}
