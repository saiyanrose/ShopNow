package com.shopme.checkout;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.address.AddressService;
import com.shopme.cart.CartService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.security.CustomerOauthUser;
import com.shopme.shippingrate.ShippingrateService;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private ShippingrateService shippingrateService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@GetMapping("/checkout")
	public String showCheckoutPage(Model model,HttpServletRequest request) {
		Customer customer=getAuthenticatedCustomer(request);			
		Address defaultAddress=addressService.getDefaultAddress(customer);	
		
		ShippingRate rate=null;
		if(defaultAddress!=null) {
			model.addAttribute("shippingAddress",defaultAddress.toString());
			rate=shippingrateService.getShippingRateForAddress(defaultAddress);
		}else {			
			model.addAttribute("shippingAddress",customer.getAddress());
			rate=shippingrateService.getShippingRateForCustomer(customer);
		}		
		
		if(rate==null) {
			return "redirect:/cart";
		}
		
		List<CartItem>cartItems=cartService.listItem(customer);	
		CheckoutInfo checkoutInfo=checkoutService.prepareCheckout(cartItems, rate);
		
		model.addAttribute("pageTitle","Checkout");
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);
		
		return "checkout/checkout";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request){
		String email=emailOfAuthenticatedCustomer(request);		
		return customerService.getCustomerbyEmail(email);
	}
	
	private String emailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal=request.getUserPrincipal();
		
		if(principal==null) return null;
		
		String customerEmail=null;
		if(principal instanceof UsernamePasswordAuthenticationToken || 
				principal instanceof RememberMeAuthenticationToken) {
			
			customerEmail=request.getUserPrincipal().getName();
		}else if(principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oAuth2AuthenticationToken=(OAuth2AuthenticationToken) principal;
			CustomerOauthUser customerOauthUser=(CustomerOauthUser) oAuth2AuthenticationToken.getPrincipal();
			customerEmail=customerOauthUser.getEmail();
		}		
		return customerEmail;
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request,Model model) {
		String paymentType=request.getParameter("paymentMethod");
		PaymentMethod paymentMethod=PaymentMethod.valueOf(paymentType);
		
		Customer customer=getAuthenticatedCustomer(request);			
		Address defaultAddress=addressService.getDefaultAddress(customer);		
		
		ShippingRate rate=null;
		if(defaultAddress!=null) {			
			rate=shippingrateService.getShippingRateForAddress(defaultAddress);
		}else {			
			rate=shippingrateService.getShippingRateForCustomer(customer);
		}		
		
		List<CartItem>cartItems=cartService.listItem(customer);	
		CheckoutInfo checkoutInfo=checkoutService.prepareCheckout(cartItems, rate);
		
		orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
		
		cartService.DeleteCartByCustomer(customer);
		
		model.addAttribute("pageTitle","Order Placed");
		return "checkout/order_completed";
	}
	
}
