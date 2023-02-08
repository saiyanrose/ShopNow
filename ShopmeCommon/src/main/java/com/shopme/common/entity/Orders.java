package com.shopme.common.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "orders")
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 45)
	private String firstName;

	@Column(nullable = false, length = 45)
	private String lastName;

	@Column(nullable = false, length = 15)
	private String phoneNumber;

	@Column(nullable = false, length = 64)
	private String addressLine1;

	@Column(length = 45)
	private String addressLine2;

	@Column(nullable = false, length = 45)
	private String city;

	@Column(nullable = false, length = 45)
	private String state;

	@Column(nullable = false, length = 10)
	private String postalCode;

	@Column(nullable = false, length = 45)
	private String country;

	private Date orderTime;

	private float shippingCost;

	private float productCost;

	private float subTotal;

	private float tax;

	private float total;

	private int deliverDays;

	private Date deliverDate;

	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL,orphanRemoval = true)
	private Set<OrderDetails> orderDetails = new HashSet<>();

	@OneToMany(mappedBy = "orders",cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderTrack> orderTracks = new ArrayList<OrderTrack>();

	public Orders() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public List<OrderTrack> getOrderTracks() {
		return orderTracks;
	}

	public void setOrderTracks(List<OrderTrack> orderTracks) {
		this.orderTracks = orderTracks;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public float getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}

	public float getProductCost() {
		return productCost;
	}

	public void setProductCost(float productCost) {
		this.productCost = productCost;
	}

	public float getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(float subTotal) {
		this.subTotal = subTotal;
	}

	public float getTax() {
		return tax;
	}

	public void setTax(float tax) {
		this.tax = tax;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public int getDeliverDays() {
		return deliverDays;
	}

	public void setDeliverDays(int deliverDays) {
		this.deliverDays = deliverDays;
	}

	public Date getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public OrderStatus getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Set<OrderDetails> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Set<OrderDetails> orderDetails) {
		this.orderDetails = orderDetails;
	}

	@Transient
	public String getDestination() {
		String destination = city + ", ";
		if (state != null && !state.isEmpty()) {
			destination += state + ", ";
		}
		if (country != null && !country.isEmpty()) {
			destination += country + ".";
		}
		return destination;
	}

	public void copyAddressFromCustomer() {
		setFirstName(customer.getFirstname());
		setLastName(customer.getLastname());
		setPhoneNumber(customer.getPhoneNumber());
		setAddressLine1(customer.getAddressLine1());
		setAddressLine2(customer.getAddressLine2());
		setCity(customer.getCity());
		setCountry(customer.getCountry().getName());
		setPostalCode(customer.getPostalCode());
		setState(customer.getState());
	}

	public void copyShippingAddress(Address address) {
		setFirstName(address.getFirstName());
		setLastName(address.getLastName());
		setPhoneNumber(address.getPhoneNumber());
		setAddressLine1(address.getAddressLine1());
		setAddressLine2(address.getAddressLine2());
		setCity(address.getCity());
		setCountry(address.getCountry().getName());
		setPostalCode(address.getPostalCode());
		setState(address.getState());
	}

	@Transient
	public String getShippingAddress() {
		String address = firstName;
		if (lastName != null && !lastName.isEmpty()) {
			address += " " + lastName;
		}
		if (addressLine1 != null && !addressLine1.isEmpty()) {
			address += ", " + addressLine1;
		}
		if (addressLine2 != null && !addressLine2.isEmpty()) {
			address += ", " + addressLine2;
		}
		if (city != null && !city.isEmpty()) {
			address += ", " + city;
		}
		if (state != null && !state.isEmpty()) {
			address += ", " + state;
		}
		address += ", " + country;
		if (postalCode != null && !postalCode.isEmpty()) {
			address += ". Postal Code: " + postalCode;
		}
		if (phoneNumber != null && !phoneNumber.isEmpty()) {
			address += ". Phone Number: " + phoneNumber;
		}
		return address;
	}
	
	@Transient
	public String getDeliverDateOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");		
		return dateFormatter.format(this.deliverDate);
	}
	
	public void setDeliverDateOnForm(String dateString) {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");		
		try {
			this.deliverDate = dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} 		
	}

	@Override
	public String toString() {
		return "Orders [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber="
				+ phoneNumber + ", addressLine1=" + addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city
				+ ", state=" + state + ", postalCode=" + postalCode + ", country=" + country + ", orderTime="
				+ orderTime + ", shippingCost=" + shippingCost + ", productCost=" + productCost + ", subTotal="
				+ subTotal + ", tax=" + tax + ", total=" + total + ", deliverDays=" + deliverDays + ", deliverDate="
				+ deliverDate + ", paymentMethod=" + paymentMethod + ", orderStatus=" + orderStatus + ", customer="
				+ customer + ", orderDetails=" + orderDetails + ", orderTracks=" + orderTracks + "]";
	}
	
	@Transient
	public boolean isCOD() {
		return paymentMethod.equals(PaymentMethod.COD);
	}
	
	@Transient
	public boolean isProcessing() {
		return hasStatus(OrderStatus.PROCESSING);
	}
	
	@Transient
	public boolean isPicked() {
		return hasStatus(OrderStatus.PICKED);
	}
	
	@Transient
	public boolean isShipping() {
		return hasStatus(OrderStatus.SHIPPING);
	}
	
	@Transient
	public boolean isRETURNED_REQUESTED() {
		return hasStatus(OrderStatus.RETURNED_REQUESTED);
	}
	
	@Transient
	public boolean isDelivered() {
		return hasStatus(OrderStatus.DELIVERED);
	}
	
	@Transient
	public boolean isReturned() {
		return hasStatus(OrderStatus.RETURNED);
	}
	
	public boolean hasStatus(OrderStatus orderStatus) {
		for(OrderTrack aTrack:orderTracks) {
			if(aTrack.getOrderStatus().equals(orderStatus)) {
				return true;
			}
		}
		return false;
		
	}
	
	@Transient
	public String getProductNames() {
		String productNames="";
		productNames="<ul>";
		for(OrderDetails details:orderDetails) {
			productNames+="<li>" + details.getProduct().getName()+"</li>";
		}
		productNames+="</ul>";
		return productNames;
	}

}
