package com.shopme.cart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.security.CustomerOauthUser;

@Controller
public class CartController {

	@Autowired
	private CartService cartService;
	
	@Autowired
	private CustomerService customerService;
	
	@GetMapping("/cart")
	public String viewCart(HttpServletRequest request,Model model) {
		Customer customer=getAuthenticatedCustomer(request);
		List<CartItem>cartItems=cartService.listItem(customer);
		float subTotal=0.0F;
		for(CartItem item:cartItems) {
			subTotal+=item.getSubTotal();
		}
		model.addAttribute("estimatedTotal",subTotal);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("pageTitle","Cart");
		return "cart/shopping_cart";
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
