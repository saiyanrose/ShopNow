package com.shopme.common.entity;

public class OrderReturnRequest {

	private Integer orderId;
	private String reason;
	private String notes;

	public OrderReturnRequest() {

	}

	public OrderReturnRequest(Integer orderId, String reason, String notes) {		
		this.orderId = orderId;
		this.reason = reason;
		this.notes = notes;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
