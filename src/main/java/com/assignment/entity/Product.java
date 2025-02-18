package com.assignment.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class Product implements Serializable{
	@Id
	@Column(name = "item_id")
	String id;
	@Column(name = "item_type")
	String type;
	@Column(name = "item_name")
	String name;
	@Column(name = "item_image")
	String image;
	@Column(name = "item_price")
	Double price;
	@Temporal(TemporalType.DATE)
	@Column(name = "item_register")
	Date registerDate = new Date();
	@Column(name = "item_description")
	String description;
	@Column(name = "item_rating")
	Float rating;
	Boolean available;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	Category category;
	@OneToMany(mappedBy = "product")
	List<OrderDetail> orderDetails;
}
