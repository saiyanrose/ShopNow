package com.shopme.common.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "order_track")
public class OrderTrack {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Date updatedTime;
	
	private String notes;

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Enumerated(EnumType.STRING)
	@Column(length = 45, nullable = false)
	private OrderStatus orderStatus;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Orders orders;

	public OrderTrack() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}
	
	@Transient
	public String getUpdatedTimeOnForm() {
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		return dateFormat.format(this.updatedTime);
	}
	
	public void setUpdatedTimeOnForm(String dateString) {
		DateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		try {
			this.updatedTime=dateFormat.parse(dateString);
		}catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
