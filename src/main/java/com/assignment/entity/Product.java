package com.assignment.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Data
public class Product implements Serializable{
	@Id
	@Column(name = "item_id")
	@GeneratedValue(strategy = GenerationType.UUID)
	String id;
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
	Double rating;
	Boolean available;
	
	@Transient // Không map vào DB, chỉ dùng để bind dữ liệu từ form
	List<Integer> itemTypeIds;
//	@Version
//    private Integer version ; 
	@ManyToOne
	@JoinColumn(name = "category_id")
	Category category;
	@OneToMany(mappedBy = "product")
	List<OrderDetail> orderDetails;
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	List<ProductItemType> productItemType;
	@OneToMany(mappedBy = "product")
	List<Cart> carts;
}
