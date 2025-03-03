package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.ItemType;

public interface ItemTypeDAO extends JpaRepository<ItemType, Integer>{
	
}
