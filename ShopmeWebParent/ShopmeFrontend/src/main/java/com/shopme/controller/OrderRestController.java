package com.shopme.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.OrderReturnRequest;
import com.shopme.common.entity.OrderReturnResponse;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.security.CustomerOauthUser;

@RestController
public class OrderRestController {

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/orders/return")
	public ResponseEntity<?>handleOrderReturnRequest(@RequestBody OrderReturnRequest orderReturnRequest,HttpServletRequest request) throws CustomerNotFoundException{
		
		Customer customer=null;
		customer= getAuthenticatedCustomer(request);
		try {
			orderService.setOrderReturnRequested(orderReturnRequest,customer);
		} catch (OrderNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new OrderReturnResponse(orderReturnRequest.getOrderId()),HttpStatus.OK);
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
	
}
