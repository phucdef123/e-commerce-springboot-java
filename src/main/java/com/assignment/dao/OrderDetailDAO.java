package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.OrderDetail;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, String>{

}
