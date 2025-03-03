package com.assignment.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.assignment.entity.Order;
import com.assignment.entity.OrderDetail;

public interface OrderDAO extends JpaRepository<Order, String>{
	//COALESCE(..., 0): Nếu SUM() hoặc MAX() trả về NULL, nó sẽ được thay thế bằng 0 để tránh lỗi null
	@Query("""
		    SELECT COALESCE(SUM(p.price * od.quantity), 0) + COALESCE(MAX(o.shippingFee), 0)
		    FROM OrderDetail od 
		    JOIN od.order o 
		    JOIN od.product p
		    WHERE o.id = :orderId
		    GROUP BY o.id
		""")
	Double getTotalAmount(@Param("orderId") String orderId);

	@Query("""
		    SELECT o FROM Order o
		    LEFT JOIN OrderDetail od ON o.id = od.order.id
		    LEFT JOIN Product p ON p.id = od.product.id
		    GROUP BY o.id, o.shippingFee, o.deliveryDate, o.orderDate, o.status, o.user.username
		    ORDER BY SUM(p.price * od.quantity) + MAX(o.shippingFee) ASC
		""")
	Page<Order> sortByTotalPriceAsc(Pageable pageable);

	@Query("""
		    SELECT o FROM Order o
		    LEFT JOIN OrderDetail od ON o.id = od.order.id
		    LEFT JOIN Product p ON p.id = od.product.id
		    GROUP BY o.id, o.shippingFee, o.deliveryDate, o.orderDate, o.status, o.user.username
		    ORDER BY SUM(p.price * od.quantity) + MAX(o.shippingFee) DESC
		""")
	Page<Order> sortByTotalPriceDesc(Pageable pageable);
	
	@Query("FROM OrderDetail od JOIN od.order o WHERE o.id = ?1")
	List<OrderDetail> findListOrderDetailById(String orderId);
	List<Order> findByUserUsername(String username);
 }
