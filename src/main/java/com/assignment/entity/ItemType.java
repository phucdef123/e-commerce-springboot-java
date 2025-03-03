package com.assignment.entity;


import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "item_type")
@Data
public class ItemType implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "type_id")
	Integer typeId;
	@Column(name = "type_name")
	String typeName;
	@OneToMany(mappedBy = "itemType")
	List<ProductItemType> productItemType;
}
