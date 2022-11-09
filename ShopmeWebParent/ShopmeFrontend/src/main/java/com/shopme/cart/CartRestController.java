package com.shopme.cart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.security.CustomerOauthUser;

@RestController
public class CartRestController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity")Integer quantity,HttpServletRequest request){		
		try {
			Customer customer=getAuthenticatedCustomer(request);
			Integer updatedQuantity=cartService.addProduct(productId, quantity, customer);
			return updatedQuantity + "item(s) were added in your cart.";
		}catch (CustomerNotFoundException e) {
			return "You must login to add product in your cart";
		}
		catch (ShopnowCartException e) {
			return "Could not items more than 5";
		}
		
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email=emailOfAuthenticatedCustomer(request);
		if(email==null) {
			throw new CustomerNotFoundException("No authenticated customer");
		}
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

	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity")Integer quantity,HttpServletRequest request){		
		try {
			Customer customer=getAuthenticatedCustomer(request);
			float subTotal=cartService.updateQuantity(productId, quantity, customer);
			return String.valueOf(subTotal);
		}catch (CustomerNotFoundException e) {
			return "You must login to change product in your cart";
		}		
	}
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeFromCart(@PathVariable("productId")Integer productId,HttpServletRequest request) {
		try {
			Customer customer=getAuthenticatedCustomer(request);
			cartService.removeProduct(productId, customer);
			return "product has been removed from your cart";
		}catch (CustomerNotFoundException e) {
			return "You must login to change product in your cart";
		}
	}
}
