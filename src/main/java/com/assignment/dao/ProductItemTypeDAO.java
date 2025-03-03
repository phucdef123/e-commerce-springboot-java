package com.assignment.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.assignment.entity.ProductItemType;
import com.assignment.entity.ProductItemTypeId;

public interface ProductItemTypeDAO extends JpaRepository<ProductItemType, ProductItemTypeId> {

}
