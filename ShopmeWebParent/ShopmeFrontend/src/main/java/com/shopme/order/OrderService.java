package com.shopme.order;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.OrderDetails;
import com.shopme.common.entity.OrderReturnRequest;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Orders;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.Product;
import com.shopme.controller.OrderNotFoundException;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;
	
	public Orders createOrder(Customer customer,Address address,List<CartItem>cartItems,
			PaymentMethod paymentMethod,CheckoutInfo checkoutInfo) {		
		
		Orders orders=new Orders();
		
		orders.setOrderTime(new Date());
		
		if(paymentMethod.equals(PaymentMethod.PAYPAL)) {
			orders.setOrderStatus(OrderStatus.PAID);
		}else {
			orders.setOrderStatus(OrderStatus.NEW);
		}
		
		orders.setCustomer(customer);
		orders.setProductCost(checkoutInfo.getProductCost());
		orders.setSubTotal(checkoutInfo.getProductTotal());
		orders.setShippingCost(checkoutInfo.getShippingCostTotal());
		orders.setTax(0.0f);
		orders.setTotal(checkoutInfo.getPaymentTotal());
		orders.setPaymentMethod(paymentMethod);
		orders.setDeliverDays(checkoutInfo.getDeliveryDays());
		orders.setDeliverDate(checkoutInfo.getDeliveryDate());
		
		if(address==null) {
			orders.copyAddressFromCustomer();
		}else {
			orders.copyShippingAddress(address);
		}
		
		Set<OrderDetails>details=orders.getOrderDetails();
		
		for(CartItem item:cartItems) {
			Product product=item.getProduct();
			
			OrderDetails orderDetails=new OrderDetails();
			orderDetails.setOrders(orders);
			orderDetails.setProduct(product);
			orderDetails.setQuantity(item.getQuantity());
			orderDetails.setUnitPrice(product.getDiscountPrize());
			orderDetails.setProductCost(item.getSubTotal() * item.getQuantity());
			orderDetails.setShippingCost(item.getShippingCost());
			orderDetails.setSubTotal(orderDetails.getProductCost());
			details.add(orderDetails);
		}
		return orderRepository.save(orders);
	}

	public Page<Orders> customerOrders(Customer customer, Integer pageNum, String sortDir, String sortField,
			String keyword) {
		
		Sort sort=Sort.by(sortField);
		sort=sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1,5,sort);
		if(keyword!=null) {
			return orderRepository.findAll(keyword,customer.getId(),pageable);
		}
		return orderRepository.findAll(customer.getId(),pageable);			
	}

	public Orders get(Integer id) throws OrderNotFoundException {	
		try {
			return orderRepository.findById(id).get();
		}catch (NoSuchElementException e) {
			throw new OrderNotFoundException("Order not found with id "+id);
		}	
	}
	
	public void setOrderReturnRequested(OrderReturnRequest orderReturnRequest,Customer customer) throws OrderNotFoundException {
		Orders orders=orderRepository.findByIdAndCustomer(orderReturnRequest.getOrderId(),customer.getId());
		if(orders==null) {
			throw new OrderNotFoundException("Order not found with id "+orderReturnRequest.getOrderId());
		}
		if(orders.isRETURNED_REQUESTED()) return;
		OrderTrack orderTrack=new OrderTrack();
		orderTrack.setOrders(orders);
		orderTrack.setUpdatedTime(new Date());
		orderTrack.setOrderStatus(OrderStatus.RETURNED_REQUESTED);
		String notes="Reason: " +orderReturnRequest.getNotes();
		orderTrack.setNotes(notes);
		
		orders.getOrderTracks().add(orderTrack);
		orders.setOrderStatus(OrderStatus.RETURNED_REQUESTED);
		
		orderRepository.save(orders);
	}
}
