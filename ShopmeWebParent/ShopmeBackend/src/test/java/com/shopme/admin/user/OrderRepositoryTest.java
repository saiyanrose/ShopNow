package com.shopme.admin.user;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.OrderDetails;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Orders;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
@Rollback(false)
public class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	public void addOrderTest() {
		Customer customer=entityManager.find(Customer.class,2);
		Product product=entityManager.find(Product.class,4);
		
		Orders orders=new Orders();
		orders.setCustomer(customer);
		orders.setFirstName(customer.getFirstname());
		orders.setLastName(customer.getLastname());
		orders.setPhoneNumber(customer.getPhoneNumber());
		orders.setAddressLine1(customer.getAddressLine1());
		orders.setAddressLine2(customer.getAddressLine2());
		orders.setCity(customer.getCity());
		orders.setState(customer.getState());
		orders.setCountry(customer.getCountry().getName());
		orders.setPostalCode(customer.getPostalCode());
		
		orders.setShippingCost(10);
		orders.setProductCost(product.getCost());
		orders.setTax(0);
		orders.setSubTotal(product.getPrice());
		orders.setTotal(product.getPrice() +10);
		
		orders.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		orders.setOrderStatus(OrderStatus.NEW);
		orders.setDeliverDate(new Date());
		orders.setDeliverDays(1);
		
		OrderDetails orderDetails=new OrderDetails();
		orderDetails.setProduct(product);
		orderDetails.setOrders(orders);
		orderDetails.setProductCost(product.getCost());
		orderDetails.setShippingCost(10);
		orderDetails.setQuantity(1);
		orderDetails.setSubTotal(product.getPrice());
		orderDetails.setUnitPrice(product.getPrice());
		
		orders.getOrderDetails().add(orderDetails);
		
		orderRepository.save(orders);
	}
	
	@Test
	public void testTrackingOrder() {
		Integer order_id=25;
		Orders order=orderRepository.findById(order_id).get();		
		OrderTrack orderTrack=new OrderTrack();
		orderTrack.setOrders(order);
		orderTrack.setUpdatedTime(new Date());
		orderTrack.setOrderStatus(OrderStatus.PICKED);
		orderTrack.setNotes(OrderStatus.PICKED.defaultDescription());
		
		List<OrderTrack>orderTracks=order.getOrderTracks();
		orderTracks.add(orderTrack);
		
		orderRepository.save(order);
	}
}
