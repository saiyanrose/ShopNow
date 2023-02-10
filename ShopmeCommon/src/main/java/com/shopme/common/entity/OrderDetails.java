package com.shopme.common.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "order_details")
public class OrderDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private int quantity;

	private float productCost;

	private float shippingCost;

	private float unitPrice;

	private float subTotal;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Orders orders;

	public OrderDetails() {

	}

	public OrderDetails(String categoryName,int quantity, float productCost, float shippingCost, float subTotal) {
		this.product=new Product();
		this.product.setCategory(new Category(categoryName));
		this.quantity = quantity;
		this.productCost = productCost;
		this.shippingCost = shippingCost;
		this.subTotal = subTotal;
	}
	
	public OrderDetails(int quantity,String productName, float productCost, float shippingCost, float subTotal) {
		this.product=new Product(productName);		
		this.quantity = quantity;
		this.productCost = productCost;
		this.shippingCost = shippingCost;
		this.subTotal = subTotal;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public float getProductCost() {
		return productCost;
	}

	public void setProductCost(float productCost) {
		this.productCost = productCost;
	}

	public float getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}

	public float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public float getSubTotal() {
		return quantity*productCost;
	}

	public void setSubTotal(float subTotal) {
		this.subTotal = subTotal;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Orders getOrders() {
		return orders;
	}

	public void setOrders(Orders orders) {
		this.orders = orders;
	}	
}
