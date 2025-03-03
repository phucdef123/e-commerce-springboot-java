package com.assignment.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assignment.entity.OrderDetail;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, String>{
	@Query("SELECT new map(FORMAT(o.deliveryDate, 'yyyy-MM') AS month, " +
           "c.categoryName AS category, SUM(od.quantity * od.price) AS revenue) " +
           "FROM Order o " +
           "JOIN o.orderDetails od " +
           "JOIN od.product p " +
           "JOIN p.category c " +
           "WHERE o.status = 'Delivered' " +
           "GROUP BY FORMAT(o.deliveryDate, 'yyyy-MM'), c.categoryName " +
           "ORDER BY month, c.categoryName")
    List<Map<String, Object>> getRevenueByMonth();
	
	@Query("SELECT c.categoryName AS category, SUM(od.quantity) AS totalQuantity " +
	           "FROM OrderDetail od " +
	           "JOIN od.product p " +
	           "JOIN p.category c " +
	           "JOIN od.order o " +
	           "WHERE o.status = 'Delivered' " +
	           "AND YEAR(o.deliveryDate) = :year " +
	           "GROUP BY c.categoryName " +
	           "ORDER BY totalQuantity DESC")
    List<Object[]> getCategorySales(@Param("year") int year);
    
    @Query("SELECT SUM(od.quantity * od.price) " +
	       "FROM OrderDetail od " +
	       "JOIN od.order o " +
	       "WHERE o.status = 'Delivered'")
	Double getTotalRevenue();
    
    @Query("SELECT SUM(od.quantity) " +
	       "FROM OrderDetail od " +
	       "JOIN od.order o " +
	       "WHERE o.status = 'Delivered' " +
	       "AND YEAR(o.deliveryDate) = :year")
	Long getTotalQuantitySold(@Param("year") int year);

    @Query("SELECT new map(u.fullname AS name, " +
	       "SUM(od.quantity * od.price) AS totalSpent, " +
	       "MIN(o.orderDate) AS firstPurchase, " +
	       "MAX(o.orderDate) AS lastPurchase) " +
	       "FROM Order o " +
	       "JOIN o.user u " +
	       "JOIN o.orderDetails od " +
	       "WHERE o.status = 'Delivered' " +
	       "GROUP BY u.fullname " +
	       "ORDER BY totalSpent DESC " +
	       "LIMIT 10")
	List<Map<String, Object>> getTopVipCustomers();


}
