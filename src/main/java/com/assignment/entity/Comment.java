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

@Data
@Entity
public class Comment implements Serializable {
	@Id
	@Column(name = "comment_id")
	String commentId;
	@Column(name = "comment_text")
	String commentText;
	String email;//bỏ
	int rating;
	@Temporal(TemporalType.DATE)
	@Column(name = "comment_date")
	Date commentDate = new Date();
	@ManyToOne
	@JoinColumn(name = "item_id")
	Product product;
	@ManyToOne
	@JoinColumn(name = "user_id")
	User user;
}
