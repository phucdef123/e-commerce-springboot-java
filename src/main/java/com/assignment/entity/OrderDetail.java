package com.assignment.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data 
@Entity
@Table(name = "order_detail")
public class OrderDetail implements Serializable{
	@Id 
	@Column(name = "order_detail_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	String id; 
	Double price; 
	Integer quantity; 
	@ManyToOne @JoinColumn(name = "item_id") 
	Product product; 
	@ManyToOne @JoinColumn(name = "order_id") 
	Order order;

}
