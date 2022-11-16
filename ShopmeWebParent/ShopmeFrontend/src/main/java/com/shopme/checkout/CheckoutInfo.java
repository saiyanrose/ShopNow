package com.shopme.checkout;

import java.util.Calendar;
import java.util.Date;

public class CheckoutInfo {

	private float productCost;

	private float productTotal;

	private float shippingCostTotal;

	private float paymentTotal;

	private int deliveryDays;

	private int deliveryDate;

	private boolean codSupported;

	public CheckoutInfo() {

	}

	public float getProductCost() {
		return productCost;
	}

	public void setProductCost(float productCost) {
		this.productCost = productCost;
	}

	public float getProductTotal() {
		return productTotal;
	}

	public void setProductTotal(float productTotal) {
		this.productTotal = productTotal;
	}

	public float getShippingCostTotal() {
		return shippingCostTotal;
	}

	public void setShippingCostTotal(float shippingCostTotal) {
		this.shippingCostTotal = shippingCostTotal;
	}

	public float getPaymentTotal() {
		return paymentTotal;
	}

	public void setPaymentTotal(float paymentTotal) {
		this.paymentTotal = paymentTotal;
	}

	public int getDeliveryDays() {
		return deliveryDays;
	}

	public void setDeliveryDays(int deliveryDays) {
		this.deliveryDays = deliveryDays;
	}

	public Date getDeliveryDate() {
		Calendar calendar=Calendar.getInstance();
		calendar.add(Calendar.DATE, deliveryDays);
		return calendar.getTime();
	}

	public void setDeliveryDate(int deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public boolean isCodSupported() {
		return codSupported;
	}

	public void setCodSupported(boolean codSupported) {
		this.codSupported = codSupported;
	}

}
