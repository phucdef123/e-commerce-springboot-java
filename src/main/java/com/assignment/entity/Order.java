package com.assignment.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data 
@Entity  
@Table(name = "[order]")
public class Order implements Serializable{
	 @Id 
	 @Column(name = "order_id")
	 @GeneratedValue(strategy = GenerationType.UUID)
	 String id; 
	 
	 //String address; 
	 @Temporal(TemporalType.DATE) 
	 @Column(name = "order_date") 
	 Date orderDate = new Date(); 
	 @Temporal(TemporalType.DATE) 
	 @Column(name = "delivery_date") 
	 Date deliveryDate = new Date();
	 @Column(name = "shipping_fee") 
	 Double shippingFee;
	 String status;
	 @ManyToOne @JoinColumn(name = "username") 
	 User user; 
	 @OneToMany(mappedBy = "order") 
	 List<OrderDetail> orderDetails; 
}
