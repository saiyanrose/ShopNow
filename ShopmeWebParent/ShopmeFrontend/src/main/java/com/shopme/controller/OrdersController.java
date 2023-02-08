package com.shopme.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Orders;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.security.CustomerOauthUser;

@Controller
public class OrdersController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;

	@GetMapping("/orders")
	public String customerOrders(Model model,HttpServletRequest request) {
		return customerOrdersByPage("id","asc",null,1,model, request);		
	}
	
	@GetMapping("orders/page/{pageNum}")
	public String customerOrdersByPage(@RequestParam(required = false, name = "sortField") String sortField,
			@RequestParam(required = false, name = "sortDir") String sortDir,
			@RequestParam(required = false, name = "keyword") String keyword,
			@PathVariable("pageNum")Integer pageNum,Model model,HttpServletRequest request) {
		
		Customer customer=getAuthenticatedCustomer(request);			
		Page<Orders>customerOrders=orderService.customerOrders(customer,pageNum,sortDir,sortField,keyword);
		List<Orders>ordersByList=customerOrders.getContent();
		
		long startCount = (pageNum - 1) * 5 + 1;		
		long endCount = startCount + 5 - 1;
		
		String reverseSort = sortDir.equals("asc") ? "desc" : "asc";
		
		if (endCount > customerOrders.getTotalElements()) {
			endCount = customerOrders.getTotalElements();
		}

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", customerOrders.getTotalElements());
		model.addAttribute("totalPage", customerOrders.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort", reverseSort);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listOrders", ordersByList);
		model.addAttribute("pageTitle","Customer Orders");
		return "orders/orders";
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
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request) {		
		try {
			Orders orders = orderService.get(id);			
			model.addAttribute("order", orders);
			return "orders/orders_detail_modal";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());			
			return "redirect:/orders";
		}
	}
}
