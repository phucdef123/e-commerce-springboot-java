package com.assignment.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data 
@Entity  
public class User implements Serializable{
	@Id 
	@Column(name = "user_id") 
	 String username; 
	 String password; 
	 @Column(name = "full_name") 
	 String fullname; 
	 String email; 
	 //String photo; 
	 boolean activated; 
	 boolean admin; 
	 @Column(name = "home_address") 
	 String homeAddress;
	 @Column(name = "work_address") 
	 String workAddress;
	 @OneToMany(mappedBy = "user") 
	 List<Order> orders; 
}
