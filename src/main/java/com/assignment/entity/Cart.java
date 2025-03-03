package com.assignment.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Cart implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id")
    private String cartId;
	int quantity;
	@ManyToOne
	@JoinColumn(name = "item_id")
	Product product;
	@ManyToOne
	@JoinColumn(name = "username")
	User user;
}
