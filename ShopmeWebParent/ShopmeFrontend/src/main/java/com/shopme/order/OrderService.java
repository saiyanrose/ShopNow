package com.shopme.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.OrderDetails;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.Orders;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.Product;

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
			orderDetails.setUnitPrice(product.getDiscountPrice());
			orderDetails.setProductCost(item.getSubTotal() * item.getQuantity());
			orderDetails.setShippingCost(item.getShippingCost());
			
			details.add(orderDetails);
		}
		return orderRepository.save(orders);
	}
}
